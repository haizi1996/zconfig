package com.hailin.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.hailin.admin.dao.CandidateDao;
import com.hailin.admin.dao.CandidateSnapshotDao;
import com.hailin.admin.dao.FileDeleteDao;
import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.exception.ConfigExistException;
import com.hailin.admin.exception.FileDeletingException;
import com.hailin.admin.exception.ModificationDuringPublishingException;
import com.hailin.admin.exception.ModifiedException;
import com.hailin.admin.exception.OnePersonPublishException;
import com.hailin.admin.exception.StatusMismatchException;
import com.hailin.admin.model.ApplyResult;
import com.hailin.admin.model.Conflict;
import com.hailin.server.common.bean.KeyValuePair;
import com.hailin.admin.service.ApplyQueueService;
import com.hailin.admin.service.CheckEnvConflictService;
import com.hailin.admin.service.ConfigService;
import com.hailin.admin.service.FileCommentService;
import com.hailin.admin.service.FileContentMD5Service;
import com.hailin.admin.service.FileTemplateService;
import com.hailin.admin.service.FileValidateUrlService;
import com.hailin.admin.service.GreyReleaseService;
import com.hailin.admin.service.InheritConfigService;
import com.hailin.admin.service.UserContextService;
import com.hailin.admin.support.UUIDUtil;
import com.hailin.server.common.bean.Candidate;
import com.hailin.server.common.bean.CandidateSnapshot;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.zconfig.client.impl.MapConfig;
import com.hailin.zconfig.client.validate.MapperHolder;
import com.hailin.zconfig.common.bean.StatusType;
import com.hailin.zconfig.common.util.FileChecker;
import com.hailin.zconfig.common.util.Numbers;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class ApplyQueueServiceImpl implements ApplyQueueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyQueueServiceImpl.class);

    private static final ObjectMapper mapper = MapperHolder.getObjectMapper();

    @Resource
    private FileTemplateService fileTemplateService;

    @Resource
    private CandidateSnapshotDao candidateSnapshotDao;

    @Resource
    private FileContentMD5Service fileContentMD5Service;

    @Resource
    private GreyReleaseService greyReleaseService;

    @Resource
    private ConfigService configService;

    @Resource
    private UserContextService userContextService;

    @Resource
    private CandidateDao candidateDao;
    
    @Resource
    private FileDeleteDao fileDeleteDao ;

    @Resource
    private InheritConfigService inheritConfigService ;

    @Resource
    private FileValidateUrlService fileValidateUrlService;

    @Resource
    private CheckEnvConflictService checkEnvConflictService;

    @Resource
    private FileCommentService fileCommentService;;

    @Override
    public ApplyResult apply(CandidateDTO dto) {
        ApplyResult applyResult = ApplyResult.of(dto.getEditVersion());
        ConfigMeta meta = new ConfigMeta(dto.getGroup(), dto.getDataId(), dto.getProfile());

        Optional<KeyValuePair<String, String>> originTemplate = fileTemplateService.getTemplate(dto.getGroup(), dto.getDataId());
//        String theData = applyPreProcess(dto, originTemplate);
        String theData = dto.getData();
        Candidate candidate;
        if (applyResult == ApplyResult.NEW) {
            candidate = new Candidate(dto.getGroup(), dto.getDataId(), dto.getProfile());
            newConfigApplyCheck(meta);
            newConfigApplyRelationInfoProcess(dto, originTemplate);

            try {
                candidateDao.save(candidate);
            } catch (DuplicateKeyException e) {
                throw new ModifiedException();
            }
        }else{
            CandidateSnapshot oldSnapshot = candidateSnapshotDao.find(
                    dto.getGroup(),
                    dto.getDataId(),
                    dto.getProfile(),
                    dto.getEditVersion());
            if (oldSnapshot.getStatus() == StatusType.PASSED) {
                throw new StatusMismatchException();
            }

            correctData(dto, oldSnapshot);
            candidate = updateCandidate(oldSnapshot, StatusType.PENDING);
        }
        CandidateSnapshot snapshot = new CandidateSnapshot(candidate , theData , userContextService.getRtxId());
        checkValidateFile(snapshot);

        candidateSnapshotDao.save(snapshot);
        //这里只能在这里处理
        if (applyResult != ApplyResult.NEW) {
            updateCandidateMapping(dto, candidate, true);
        }

        fileContentMD5Service.applyConfigChange(snapshot);
        fileTemplateService.setPropertiesTemplate(dto);
        updateConfigComment(meta, snapshot.getEditVersion(), dto.getComment());
        return applyResult;
    }

    private void updateConfigComment(ConfigMeta meta, long version, String comment) {
        if (!Strings.isNullOrEmpty(comment)) {
            fileCommentService.setComment(meta, version, comment);
        }
    }

    private void correctData(CandidateDTO dto, CandidateSnapshot snapshot) {
        dto.setBasedVersion(snapshot.getBasedVersion());
        dto.setStatus(snapshot.getStatus());
    }

    private void checkValidateFile(CandidateSnapshot snapshot) {
        if (FileChecker.isTemplateFile(snapshot.getDataId())) {
            String validateUrl = fileValidateUrlService.getUrl(
                    new ConfigMeta(snapshot.getGroup(), snapshot.getDataId(), snapshot.getProfile()));
            if (!Strings.isNullOrEmpty(validateUrl)) {
                doCheckValidateFile(validateUrl, snapshot);
            }
        }
    }


    private final AsyncHttpClient httpClient = getHttpClient();

    private static final int DEFAULTTIMEOUT = 5000;

    private volatile int REQUESTTIMEOUTMS = 5000;

    private static final int CONNECTTIMEOUT = 2000;

    private static final int REQUESTTIMEOUT = 3000;

    private AsyncHttpClient getHttpClient() {
        MapConfig config = MapConfig.get("config.properties");
//        config.asMap();
        config.addListener(conf -> {
            REQUESTTIMEOUTMS = Numbers.toInt(conf.get("qtable.validate.timeoutMs"), DEFAULTTIMEOUT);
            LOGGER.info("qtable validate timeout ms is [{}]", REQUESTTIMEOUTMS);
        });

        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setConnectTimeout(CONNECTTIMEOUT);
        builder.setRequestTimeout(REQUESTTIMEOUT);
        return new AsyncHttpClient(builder.build());
    }

    private void doCheckValidateFile(String validateUrl, CandidateSnapshot snapshot) {
//        AsyncHttpClient.BoundRequestBuilder builder = httpClient.preparePost(validateUrl);
//        builder.setHeader("content-type", "application/json; charset=utf-8");
//        try {
//            builder.setBody(mapper.writeValueAsString(new VersionedJsonRequest<>(snapshot.getData(), 0)));
//        } catch (JsonProcessingException e) {
//            LOGGER.error("write qtable data to json error, {}", snapshot, e);
//            throw new ValidateMessageException("system error");
//        }
//        builder.setRequestTimeout(REQUESTTIMEOUTMS);
//        Request request = builder.build();
//        Response response;
//        try {
//            response = httpClient.executeRequest(request).get();
//        } catch (Exception e) {
//            LOGGER.warn("request to validate url error, {}", snapshot, e);
//            throw new ValidateMessageException("request to validate url error");
//        }
//
//        if (response.getStatusCode() != Constants.OK_STATUS) {
//            LOGGER.warn("request to validate url failOf, code is [{}], {}", response.getStatusCode(), snapshot);
//            throw new ValidateMessageException(
//                    "request to validate url failOf, code is [" + response.getStatusCode() + "]");
//        }
//
//        VersionedJsonResponse<String> r;
//        try {
//            r = mapper.readValue(response.getResponseBody("utf-8"), new TypeReference<VersionedJsonResponse<String>>() {
//            });
//        } catch (Exception e) {
//            LOGGER.warn("get response from validate url failOf, {}", snapshot, e);
//            throw new ValidateMessageException("get response from validate url failOf");
//        }
//
//        if (r.getStatus() != 0) {
//            LOGGER.warn("get response from validate url failOf, remote status is [{}], message is [{}], {}",
//                    r.getStatus(), r.getMessage(), snapshot);
//            throw new ValidateMessageException(
//                    "get response from validate url failOf, remote message is [" + r.getMessage() + "]");
//        }
//
//        QTableError qTableError;
//        try {
//            qTableError = mapper.readValue(r.getData(), QTableError.class);
//        } catch (IOException e) {
//            LOGGER.warn("read response from validate url failOf, {}", snapshot, e);
//            throw new ValidateMessageException("read response from validate url failOf");
//        }
//
//        if (qTableError.isError()) {
//            throw new ValidateErrorException(qTableError);
//        }
    }

    private void newConfigApplyRelationInfoProcess(CandidateDTO dto, Optional<KeyValuePair<String, String>> originTemplate) {

        if (!Strings.isNullOrEmpty(dto.getInheritGroupId())
                && !Strings.isNullOrEmpty(dto.getInheritDataId())) {
            inheritConfigService.save(dto, userContextService.getRtxId());
        }

    }

    private void newConfigApplyCheck(ConfigMeta meta) {
        /**
         * 检查新建的配置文件是否已经存在，如果已经存在则存在冲突
         * 有两种冲突的方式：
         * 1. 配置文件已存在，且为其它文件的引用
         * 2. 配置文件已存在，且为本环境下的实体文件
         */
        Optional<Conflict> conflictResult = checkEnvConflictService.getConflict(meta);
        if (conflictResult.isPresent()) {
            throw new ConfigExistException(conflictResult.get());
        }

        if (fileDeleteDao.exist(meta) > 0) {//是否文件正在被删除
            throw new FileDeletingException();
        }
    }


    @Override
    @Transactional
    public void delete(CandidateDTO dto) {
        CandidateSnapshot oldSnapshot = candidateSnapshotDao.find(dto.getGroup(), dto.getDataId(), dto.getProfile(),
                dto.getEditVersion());
        if (oldSnapshot.getStatus() == StatusType.DELETE) {
            throw new ModifiedException();
        }
        Candidate candidate = updateCandidate(oldSnapshot, StatusType.DELETE);
        updateCandidateMapping(dto, candidate, false);

        CandidateSnapshot snapshot = new CandidateSnapshot(candidate , oldSnapshot.getData() , userContextService.getRtxId() );
        candidateSnapshotDao.save(snapshot);
        fileContentMD5Service.applyConfigChange(snapshot);

        if (oldSnapshot.getStatus() == StatusType.PUBLISH) {
            configService.delete(snapshot);
        }
        if (FileChecker.isTemplateFile(dto.getDataId())) {
            fileTemplateService.deleteDefaultConfigId(new ConfigMeta(dto.getGroup(), dto.getDataId(), dto.getProfile()));
        }
    }

    private void updateCandidateMapping(CandidateDTO dto, Candidate candidate, boolean b) {
        //dto里只有apply/forceApply时才有template/templateGroup字段。审批/发布/删除等操作没有该字段
        if (!dto.getDataId().endsWith(".t") && !dto.getDataId().endsWith(".json")) {
            return;
        }
        Optional<KeyValuePair<String, String>> templateInfo = fileTemplateService
                .getTemplate(dto.getGroup(), dto.getDataId());
        if (!templateInfo.isPresent()) {
            return;
        }
    }

    private Candidate updateCandidate(CandidateSnapshot oldSnapshot, StatusType resultType) {
        long basedVersion = oldSnapshot.getStatus() == StatusType.PUBLISH ? oldSnapshot.getEditVersion() : oldSnapshot
                .getBasedVersion();
        Candidate candidate = new Candidate(oldSnapshot.getGroup(), oldSnapshot.getDataId(), oldSnapshot.getProfile(),
                basedVersion, oldSnapshot.getEditVersion() + 1, resultType);

        int update = candidateDao.update(candidate, oldSnapshot.getStatus() );
        if (update == 0) {
            throw new ModifiedException();
        }

        return candidate;
    }

    @Override
    public void forceApply(CandidateDTO dto) {
        ConfigMeta meta = new ConfigMeta(dto.getGroup(), dto.getDataId(), dto.getProfile());
        CandidateSnapshot oldSnapshot = candidateSnapshotDao.find(dto.getGroup(), dto.getDataId(),
                dto.getProfile(), dto.getEditVersion());

        String theData = dto.getData();
        if (oldSnapshot.getStatus() != StatusType.DELETE) {
            throw new StatusMismatchException();
        }
        correctData(dto, oldSnapshot);
        Candidate candidate = updateCandidate(oldSnapshot, StatusType.PENDING);
        CandidateSnapshot snapshot = new CandidateSnapshot(candidate, theData, userContextService.getRtxId());
        checkValidateFile(snapshot);
        updateConfigComment(meta, snapshot.getEditVersion(), dto.getComment());
        candidateSnapshotDao.save(snapshot);
        fileContentMD5Service.applyConfigChange(snapshot);
        fileTemplateService.setPropertiesTemplate(dto);
        updateCandidateMapping(dto, candidate, true);
    }

    @Override
    @Transactional
    public void reject(CandidateDTO candidate) {
        changeStatusAndSave(candidate, StatusType.PENDING, StatusType.REJECT);
    }

    private CandidateSnapshot changeStatusAndSave(CandidateDTO dto,
                                                  StatusType expectType,
                                                  StatusType resultType)
            throws ModifiedException, StatusMismatchException, OnePersonPublishException {
        CandidateSnapshot oldSnapshot = candidateSnapshotDao.find(dto.getGroup(),
                dto.getDataId(),
                dto.getProfile(),
                dto.getEditVersion());
        if (oldSnapshot.getStatus() != expectType) {
            throw new StatusMismatchException();
        }

        if (resultType == StatusType.PASSED || resultType == StatusType.PUBLISH) {
            checkValidateFile(oldSnapshot);
        }

        correctData(dto, oldSnapshot);
        Candidate candidate = updateCandidate(oldSnapshot, resultType);

        CandidateSnapshot snapshot = new CandidateSnapshot(candidate,
                oldSnapshot.getData(),
                userContextService.getRtxId());

        candidateSnapshotDao.save(snapshot);
        updateCandidateMapping(dto, candidate, false);
        fileContentMD5Service.applyConfigChange(snapshot);
        updateConfigComment(new ConfigMeta(dto.getGroup(), dto.getDataId(), dto.getProfile()), snapshot.getEditVersion(), dto.getComment());
        return snapshot;
    }

    @Override
    public CandidateSnapshot publish(CandidateDTO candidate) {
        lock(candidate);
        CandidateSnapshot candidateSnapshot = doPublish(candidate);
        unlock(candidate);
        return null;
    }

    private CandidateSnapshot doPublish(CandidateDTO candidate) {
        return configService.publish(changeStatusAndSave(candidate, StatusType.PASSED, StatusType.PUBLISH));
    }

    private void unlock(CandidateDTO candidate) {
        greyReleaseService.deleteTaskMapping(getMeta(candidate), candidate.getUuid());
    }

    private void lock(CandidateDTO candidate) {
        candidate.setUuid(UUIDUtil.generate());
        if (!greyReleaseService.insertTaskMapping(getMeta(candidate), candidate.getUuid())) {
            throw new ModificationDuringPublishingException("检测到有发布任务正在执行");
        }
    }

    private ConfigMeta getMeta(CandidateDTO dto) {
        return new ConfigMeta(dto.getGroup(), dto.getDataId(), dto.getProfile());
    }
}
