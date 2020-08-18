package com.hailin.server.config.zfile;

import com.google.common.base.Optional;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.service.ConfigInfoService;

public interface ZFileFactory {

    String getName();

    Optional<ZFile> create(ConfigMeta meta, ConfigInfoService configInfoService);

    Optional<ZFile> internalCreate(ConfigMeta meta, ConfigMeta candidate, ConfigInfoService configInfoService);

}
