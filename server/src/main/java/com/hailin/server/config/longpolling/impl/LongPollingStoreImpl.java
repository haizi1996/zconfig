package com.hailin.server.config.longpolling.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.IpAndPort;
import com.hailin.server.config.longpolling.Listener;
import com.hailin.server.config.longpolling.LongPollingStore;
import com.hailin.server.domain.Changed;
import com.hailin.zconfig.common.support.concurrent.NamedThreadFactory;
import com.hailin.zconfig.common.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class LongPollingStoreImpl implements LongPollingStore {

    private static final Logger logger = LoggerFactory.getLogger(LongPollingStoreImpl.class);

    private static ExecutorService onChangeExecutor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(), new NamedThreadFactory("config-on-change"));

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
        if (listeners.isEmpty()) {
            return;
        }

        Changed change = new Changed(meta, newVersion);
        if (listeners.size() <= pushConfig.getDirectPushLimit()) {
            directDoChange(listeners, change, type);
        } else {
            PushItem pushItem = new PushItem(listeners, type, change);
            scheduledExecutor.execute(new PushRunnable(pushItem));
        }
    }
}
