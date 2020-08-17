package com.hailin.server.config.longpolling;

import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.IpAndPort;

import java.util.Set;

public interface LongPollingStore {

//    void addListener(Listener listener);

    void manualPush(ConfigMeta meta, long version, Set<IpAndPort> ipAndPorts);

    void manualPushIps(ConfigMeta meta, long version, Set<String> ips);

    void onChange(ConfigMeta meta, long version);
}
