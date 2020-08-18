package com.hailin.server.service;

import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.MetaIp;

import java.util.Optional;

public interface CacheFixedVersionConsumerService {

    void update(MetaIp consumer, long version);

    Optional<Long> getFixedVersion(ConfigMeta meta, String ip);
}
