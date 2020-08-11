package com.hailin.admin.service.impl;

import com.hailin.admin.dao.CandidateDao;
import com.hailin.admin.dao.CandidateSnapshotDao;
import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.exception.ModifiedException;
import com.hailin.admin.model.ApplyResult;
import com.hailin.admin.model.KeyValuePair;
import com.hailin.admin.service.ApplyQueueService;
import com.hailin.admin.service.ConfigService;
import com.hailin.admin.service.FileContentMD5Service;
import com.hailin.admin.service.FileTemplateService;
import com.hailin.admin.service.UserContextService;
import com.hailin.server.common.bean.Candidate;
import com.hailin.server.common.bean.CandidateSnapshot;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.zconfig.common.bean.StatusType;
import com.hailin.zconfig.common.util.FileChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class ApplyQueueServiceImpl implements ApplyQueueService {

    @Resource
    private FileTemplateService fileTemplateService;

    @Resource
    private CandidateSnapshotDao candidateSnapshotDao;

    @Resource
    private FileContentMD5Service fileContentMD5Service;

    @Resource
    private ConfigService configService;

    @Resource
    private UserContextService userContextService;

    @Resource
    private CandidateDao candidateDao;

    @Override
    public ApplyResult apply(CandidateDTO dto) {
        ApplyResult applyResult = ApplyResult.of(dto.getEditVersion());
        ConfigMeta meta = new ConfigMeta(dto.getGroup(), dto.getDataId(), dto.getProfile());

        Optional<KeyValuePair<String, String>> originTemplate = fileTemplateService.getTemplate(dto.getGroup(), dto.getDataId());
//        String theData = applyPreProcess(dto, originTemplate);
        return null;
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
}
