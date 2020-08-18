package com.hailin.server.service.impl;

import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.service.CacheConfigVersionService;
import com.hailin.server.service.CacheService;
import com.hailin.server.service.ConfigInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.Set;

@Service
public class CacheConfigInfoService implements ConfigInfoService {

    @Resource
    private CacheConfigVersionService cacheConfigVersionService;

    @Override
    public Optional<Long> getVersion(ConfigMeta meta) {
        return cacheConfigVersionService.getVersion(meta);
    }

    @Resource
    private CacheService cacheService;

    @Override
    public Set<ConfigMeta> getChildren(ConfigMeta parent) {
        return cacheService.getChildren(parent);
    }

    @Override
    public Optional<ConfigMeta> getParent(ConfigMeta child) {
        if (getVersion(child).isPresent()) {
            return cacheService.getParent(child);
        }
        return Optional.empty();
    }
}
