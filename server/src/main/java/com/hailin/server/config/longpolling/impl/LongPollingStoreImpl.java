package com.hailin.server.config.longpolling.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.cache.Cache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.IpAndPort;
import com.hailin.server.config.longpolling.Listener;
import com.hailin.server.config.longpolling.LongPollingStore;
import com.hailin.server.service.CacheFixedVersionConsumerService;
import com.hailin.server.service.ConfigInfoService;
import com.hailin.zconfig.common.support.concurrent.NamedThreadFactory;
import com.hailin.zconfig.common.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LongPollingStoreImpl implements LongPollingStore {

    private static final Logger logger = LoggerFactory.getLogger(LongPollingStoreImpl.class);

    private static ExecutorService onChangeExecutor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(), new NamedThreadFactory("config-on-change"));

    private static final ConcurrentMap<ConfigMeta, Cache<Listener, Listener>> listenerMappings = Maps.newConcurrentMap();

    @Resource
    private ConfigInfoService cacheConfigInfoService;

    @Resource
    private CacheFixedVersionConsumerService cacheFixedVersionConsumerService;

    private static Set<String> closeFixVersionAppidSet = Sets.newHashSet();

    @Override
    public void addListener(Listener listener) {

    }

    @Override
    public void manualPush(ConfigMeta meta, long version, Set<IpAndPort> ipAndPorts) {

    }

    @Override
    public void manualPushIps(ConfigMeta meta, long version, Set<String> ips) {

    }

    @Override
    public void onChange(ConfigMeta meta, long version) {
        logger.info("file change: {}, version {}", meta, version);
        onChangeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                doChange(meta, version, Constants.UPDATE, Predicates.<Listener>alwaysTrue());
            }
        });
    }

    private void doChange(ConfigMeta meta, long newVersion, String type, Predicate<Listener> needChange) {
        List<Listener> listeners = getListeners(meta, needChange);
//        if (listeners.isEmpty()) {
//            return;
//        }
//
//        Changed change = new Changed(meta, newVersion);
//        if (listeners.size() <= pushConfig.getDirectPushLimit()) {
//            directDoChange(listeners, change, type);
//        } else {
//            PushItem pushItem = new PushItem(listeners, type, change);
//            scheduledExecutor.execute(new PushRunnable(pushItem));
//        }
    }

    private List<Listener> getListeners(ConfigMeta meta, Predicate<Listener> needChange) {
        List<Listener> selfListeners = getListenersForMeta(meta, needChange);
        List<Listener> childrenListeners = getChildrenListeners(meta, needChange);
        List<Listener> beforeFilter = ImmutableList.copyOf(Iterables.concat(selfListeners, childrenListeners));
        if (closeFixVersionAppidSet.contains(meta.getGroup())) {
            return beforeFilter;
        } else {
            return fixListeners(beforeFilter, meta);
        }
    }

    private List<Listener> fixListeners(final List<Listener> concatListeners, final ConfigMeta meta) {
        return FluentIterable.from(concatListeners).filter(
                new Predicate<Listener>() {
                    @Override
                    public boolean apply(Listener listener) {
                        String ip = listener.getContextHolder().getIp();
                        return !cacheFixedVersionConsumerService.getFixedVersion(meta, ip).isPresent();
                    }
                }
        ).toList();
    }

    private List<Listener> getChildrenListeners(ConfigMeta meta, Predicate<Listener> needChange) {
        List<Listener> listeners = Lists.newArrayList();
        Set<ConfigMeta> children = cacheConfigInfoService.getChildren(meta);
        for (ConfigMeta child : children) {
            listeners.addAll(getListenersForMeta(child, needChange));
        }
        return listeners;
    }

    private List<Listener> getListenersForMeta(ConfigMeta meta, Predicate<Listener> needChange) {
        Cache<Listener, Listener> cache = listenerMappings.get(meta);
        if (cache != null) {
            List<Listener> listeners = Lists.newArrayList();
            for (Listener listener : cache.asMap().values()) {
                if (needChange.apply(listener)) {
                    cache.invalidate(listener);
                    listeners.add(listener);
                }
            }
            cache.cleanUp();
            return listeners;
        } else {
            return ImmutableList.of();
        }
    }
}
