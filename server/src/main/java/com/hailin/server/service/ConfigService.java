package com.hailin.server.service;


import com.hailin.server.common.bean.ChecksumData;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import com.hailin.server.config.zfile.ZFile;
import com.hailin.server.config.zfile.ZFileFactory;
import com.hailin.server.exception.ConfigNotFoundException;

import java.util.Map;

public interface ConfigService {

    Map.Entry<ZFile, ChecksumData<String>> findConfig(ZFileFactory zFileFactory, VersionData<ConfigMeta> configId) throws ConfigNotFoundException;

    Map.Entry<ZFile, VersionData<ChecksumData<String>>> loadConfig(VersionData<ConfigMeta> versionMeta)
            throws ConfigNotFoundException;

}
