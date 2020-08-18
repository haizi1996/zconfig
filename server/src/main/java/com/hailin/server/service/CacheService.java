package com.hailin.server.service;

import com.hailin.server.common.bean.ConfigMeta;

import java.util.Optional;
import java.util.Set;

public interface CacheService {

    Set<ConfigMeta> getChildren(ConfigMeta parent);

    Optional<ConfigMeta> getParent(ConfigMeta childFile);
}
