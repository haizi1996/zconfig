package com.hailin.server.config.zfile.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.hailin.server.common.bean.ChecksumData;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import com.hailin.server.config.zfile.ZFile;
import com.hailin.server.config.zfile.ZFileFactory;
import com.hailin.server.exception.ConfigNotFoundException;
import com.hailin.server.service.ConfigInfoService;
import com.hailin.server.service.ConfigService;
import com.hailin.server.service.VersionChooseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private VersionChooseService versionChooseService;

    @Resource
    private ConfigInfoService cacheConfigInfoService;

    private static Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Override
    public Map.Entry<ZFile, ChecksumData<String>> findConfig(ZFileFactory zFileFactory, VersionData<ConfigMeta> configId) throws ConfigNotFoundException {
        Optional<ZFile> zFile = zFileFactory.create(configId.getData(), cacheConfigInfoService);
        if (!zFile.isPresent()) {
            LOGGER.warn("findConfig未能从内存缓存中找到配置文件的元信息, meta[{}]", configId.getData());
            throw new ConfigNotFoundException();
        }

        return Maps.immutableEntry(zFile.get(), zFile.get().findConfig(configId.getVersion()));
    }

    @Override
    public Map.Entry<ZFile, VersionData<ChecksumData<String>>> loadConfig(VersionData<ConfigMeta> versionMeta) throws ConfigNotFoundException {
        Map.Entry<ZFile, ChecksumData<String>> fileConfig = findConfig(versionChooseService.getQFileFactory(), versionMeta);
        VersionData<ChecksumData<String>> fileContent = new VersionData<>(versionMeta.getVersion(), fileConfig.getValue());
        return Maps.immutableEntry(fileConfig.getKey(), fileContent);
    }
}
