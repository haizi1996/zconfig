package com.hailin.server.service.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class CacheServiceImpl implements CacheService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    //由于引用关系被存放在多个容器，所以需要一个同步锁
    private ReadWriteLock referenceLock = new ReentrantReadWriteLock();
    private volatile ConcurrentMap<ConfigMeta, ConfigMeta> currentReferenceCache = Maps.newConcurrentMap();
    private volatile ConcurrentMap<ConfigMeta, ConfigMeta> currentChild2ParentInheritCache = Maps.newConcurrentMap();
    private volatile Map<ConfigMeta, Set<ConfigMeta>> currentParent2ChildrenInheritCache = Maps.newConcurrentMap();

    @Override
    public Set<ConfigMeta> getChildren(ConfigMeta parent) {
        Set<ConfigMeta> children = currentParent2ChildrenInheritCache.get(parent);
        return children != null ? ImmutableSet.copyOf(children) : ImmutableSet.<ConfigMeta>of();
    }

    @Override
    public Optional<ConfigMeta> getParent(ConfigMeta childFile) {
        return Optional.ofNullable(currentChild2ParentInheritCache.get(childFile));
    }
}
