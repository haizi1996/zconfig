package com.hailin.server.config.longpolling;

import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.config.longpolling.impl.AsyncContextHolder;
import com.hailin.server.domain.Changed;

public interface Listener {

    ConfigMeta getMeta();

    long getVersion();

    AsyncContextHolder getContextHolder();

    void onChange(Changed change, String type);
}
