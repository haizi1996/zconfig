package com.hailin.admin.service.impl;

import com.google.common.base.Strings;
import com.hailin.admin.dao.CandidateSnapshotDao;
import com.hailin.admin.dao.ConfigDao;
import com.hailin.admin.dao.SnapshotDao;
import com.hailin.admin.entity.ConfigInfoWithoutPublicStatus;
import com.hailin.admin.exception.ModifiedException;
import com.hailin.admin.service.ConfigService;
import com.hailin.admin.service.FileTemplateService;
import com.hailin.admin.service.template.TemplateUtils;
import com.hailin.server.common.bean.CandidateSnapshot;
import com.hailin.server.common.bean.ChecksumData;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.PublicStatus;
import com.hailin.server.common.bean.VersionData;
import com.hailin.zconfig.common.bean.StatusType;
import com.hailin.zconfig.common.util.ChecksumAlgorithm;
import com.hailin.zconfig.common.util.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hailin.zconfig.common.util.Constants.NO_FILE_VERSION;

@Service
public class ConfigServiceImpl implements ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Resource
    private CandidateSnapshotDao candidateSnapshotDao;

    @Resource
    private FileTemplateService fileTemplateService;

    private SnapshotDao snapshotDao;

    @Resource
    private ConfigDao configDao;

    @Override
    public ConfigInfoWithoutPublicStatus findPublishedConfigWithoutPublicStatus(ConfigMeta configMeta) {
        ConfigInfoWithoutPublicStatus config = configDao.findCurrentConfigInfo(configMeta);
        if (config == null || !config.isInuse()) {
            return null;
        }
        return config;
    }

    @Override
    public void delete(CandidateSnapshot snapshot) throws ModifiedException {
        ConfigMeta configMeta = new ConfigMeta(snapshot.getGroup(), snapshot.getDataId(), snapshot.getProfile());
        int deleted = configDao.delete(
                VersionData.of(snapshot.getEditVersion(), configMeta),
                snapshot.getBasedVersion());
        if (deleted == 0) {
            throw new ModifiedException();
        }
    }

    @Override
    public VersionData<String> getCurrentPublishedData(ConfigMeta meta) {
        ConfigInfoWithoutPublicStatus current = findPublishedConfigWithoutPublicStatus(meta);
        String oldData;
        if (current == null) {
            oldData = "";
        } else {
            oldData = candidateSnapshotDao.find(current.getGroup(), current.getDataId(), current.getProfile(), current.getVersion()).getData();
        }
        String data = templateDataLongToStr(meta.getGroup(), meta.getDataId(), oldData);
        return new VersionData<>(current == null ? NO_FILE_VERSION : current.getVersion(), data);
    }

    @Override
    public String templateDataLongToStr(String group, String dataId, String data) {
        if (Strings.isNullOrEmpty(data) || !FileChecker.isTemplateFile(dataId)) {
            return data;
        }
        Optional<String> detail = fileTemplateService.getTemplateDetailByFile(group, dataId);
        if (detail.isPresent()) {
            String templateDetail = detail.get();
            try {
                Optional<String> optional = TemplateUtils.processTimeLongToStr(dataId, data, templateDetail);
                if (optional.isPresent()) {
                    return optional.get();
                }
            } catch (Exception e) {
                LOGGER.warn("can note process time long to str, group is [%s], dataId is [%s]", group, dataId, e);
                return data;
            }
        }
        return data;
    }

    @Override
    public CandidateSnapshot publish(CandidateSnapshot snapshot)throws ModifiedException {
        LOGGER.info("publish config, {}", snapshot);
        checkArgument(snapshot.getStatus() == StatusType.PUBLISH, "status should be publish: %s", snapshot.getStatus());

        createOrUpdateConfig(snapshot);
        saveSnapshot(snapshot);

        return snapshot;
    }

    private void saveSnapshot(CandidateSnapshot snapshot) {
        String checksum = ChecksumAlgorithm.getChecksum(snapshot.getData());
        snapshotDao.save(snapshot.getEditVersion(),
                new ConfigMeta(snapshot.getGroup(), snapshot.getDataId(), snapshot.getProfile()),
                 checksum,snapshot.getData(),
                snapshot.getBasedVersion());
    }

    private void createOrUpdateConfig(CandidateSnapshot snapshot) {
        final ConfigMeta configMeta = new ConfigMeta(snapshot.getGroup(), snapshot.getDataId(), snapshot.getProfile());
        final ConfigInfoWithoutPublicStatus currentConfig = configDao.findCurrentConfigInfo(configMeta);
        final long version = currentConfig == null ? 0 : currentConfig.getVersion();

        if (version != snapshot.getBasedVersion()) {
            throw new ModifiedException();
        } else if (version == 0) {
            try {
                configDao.create( configMeta,snapshot.getEditVersion());
            } catch (DuplicateKeyException e) {
                throw new ModifiedException();
            }
        } else {
            int update = configDao.update(configMeta , snapshot.getEditVersion(),
                    snapshot.getBasedVersion(),
                    PublicStatus.INUSE);
            if (update == 0) {
                throw new ModifiedException();
            }
        }
    }
}
