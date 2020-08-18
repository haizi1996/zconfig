package com.hailin.server.service;

import com.hailin.server.common.bean.ConfigMeta;

import java.util.Optional;
import java.util.Set;

public interface ConfigInfoService {

    Optional<Long> getVersion(ConfigMeta meta);

    Set<ConfigMeta> getChildren(ConfigMeta meta);

    Optional<ConfigMeta> getParent(ConfigMeta child);
}
