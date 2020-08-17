package com.hailin.server.service.impl;

import com.google.common.base.Optional;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import com.hailin.server.config.longpolling.LongPollingStore;
import com.hailin.server.dao.ConfigDao;
import com.hailin.server.domain.UpdateType;
import com.hailin.server.service.CacheConfigVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class CacheConfigVersionServiceImpl implements CacheConfigVersionService {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfigVersionServiceImpl.class);

    private volatile ConcurrentHashMap<ConfigMeta ,Long> cache = new ConcurrentHashMap();

    @Resource
    private ConfigDao configDao;

    @Resource
    private LongPollingStore longPollingStore;

    @PostConstruct
    public void init(){
        freshConfigVersionCache();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("fresh-config-version-thread");
                try {
                    freshConfigVersionCache();
                } catch (Throwable e) {
                    logger.error("fresh config version error", e);
                }
            }
        }, 3, 3, TimeUnit.MINUTES);
    }

    private void freshConfigVersionCache() {
        logger.info("fresh config version cache");
        List<VersionData<ConfigMeta>> configIds = configDao.loadAll();
        ConcurrentHashMap<ConfigMeta, Long> newCache = new ConcurrentHashMap<ConfigMeta, Long>(configIds.size());
        ConcurrentHashMap<ConfigMeta, Long> oldCache = this.cache;
        synchronized (this) {
            for (VersionData<ConfigMeta> configId : configIds) {
                long newVersion = configId.getVersion();
                Long oldVersion = cache.get(configId.getData());
                // 暂时不考虑delete的情况
                // 从数据库load数据先于配置更新
                if (oldVersion != null && oldVersion > newVersion) {
                    newVersion = oldVersion;
                }
                newCache.put(configId.getData(), newVersion);
            }

            this.cache = newCache;
        }

        logger.info("fresh config version cache successOf, count [{}]", configIds.size());
        int updates = 0;
        for (Map.Entry<ConfigMeta, Long> oldEntry : oldCache.entrySet()) {
            ConfigMeta meta = oldEntry.getKey();
            Long oldVersion = oldEntry.getValue();
            Long newVersion = newCache.get(meta);
            if (newVersion != null && newVersion > oldVersion) {
                updates += 1;
                longPollingStore.onChange(meta, newVersion);
            }
        }
        logger.info("fresh size={} config version cache from db", updates);
    }

    @Override
    public Optional<Long> getVersion(ConfigMeta meta) {
        return Optional.fromNullable(cache.get(meta));
    }

    @Override
    public void update(VersionData<ConfigMeta> configId, UpdateType updateType) {
        ConfigMeta key = configId.getData();
        if (updateType == UpdateType.DELETE){
            cache.remove(key);
            return;
        }
        Long version = cache.get(key);
        long newVersion = configId.getVersion();
        if (version != null && version >= version){
            return;
        }
        synchronized (this){
            version = cache.get(key);
            if (version == null || newVersion > version){
                cache.put(key , newVersion);
                longPollingStore.onChange(key, newVersion);
            }
        }
    }
}
