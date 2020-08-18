package com.hailin.server.service.impl;

import com.google.common.collect.Maps;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.KeyValuePair;
import com.hailin.server.common.bean.MetaIp;
import com.hailin.server.dao.FixedConsumerVersionDao;
import com.hailin.server.service.CacheFixedVersionConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CacheFixedVersionConsumerServiceImpl implements CacheFixedVersionConsumerService {

    private final Logger logger = LoggerFactory.getLogger(CacheFixedVersionConsumerServiceImpl.class);

    private final static long VERSION_NOT_FIXED = -1L;

    private volatile ConcurrentMap<MetaIp, Long> cache = Maps.newConcurrentMap();

    @Resource
    private FixedConsumerVersionDao fixedConsumerVersionDao;

    @PostConstruct
    public void init(){
        refreshCache();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("fresh-fixed-version-thread");
                refreshCache();
            }
        }, 3, 3, TimeUnit.MINUTES);
    }

    private void refreshCache() {
        logger.info("start refreshing fixed version consumer cache");
        try {
            ConcurrentMap<MetaIp, Long> newCache = Maps.newConcurrentMap();
            synchronized (this) {
                List<KeyValuePair<MetaIp , Long>> pairList = fixedConsumerVersionDao.queryAll();
                if(!CollectionUtils.isEmpty(pairList)){
                    newCache.putAll(pairList.stream().collect(Collectors.toMap(KeyValuePair::getKey , KeyValuePair::getValue)));
                }
                cache = newCache;
            }
            logger.info("refreshing fixed version consumer cache successOf, total num:[{}]", newCache.size());
        } catch (Exception e) {
            logger.error("refreshing fixed version consumer cache error", e);
        }
    }

    @Override
    public void update(MetaIp consumer, long version) {
        if (version <= VERSION_NOT_FIXED) {
            cache.remove(consumer);
            logger.info("delete fixed version consumer cache, metaAndIp:{}", consumer);
        } else {
            cache.put(consumer, version);
            logger.info("update fixed version consumer cache, metaAndIp:{}, fixedVersion:{}", consumer, version);
        }
    }

    @Override
    public Optional<Long> getFixedVersion(ConfigMeta meta, String ip) {
        return Optional.ofNullable(cache.get(new MetaIp(meta, ip)));
    }
}
