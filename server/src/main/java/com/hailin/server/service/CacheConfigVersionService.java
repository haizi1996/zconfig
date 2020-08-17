package com.hailin.server.service;

import com.google.common.base.Optional;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import com.hailin.server.domain.UpdateType;

public interface CacheConfigVersionService {

    Optional<Long> getVersion(ConfigMeta meta);

    void update(VersionData<ConfigMeta> configId, UpdateType updateType);
}
