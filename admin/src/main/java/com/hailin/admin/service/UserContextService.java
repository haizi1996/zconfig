package com.hailin.admin.service;

import com.hailin.server.common.support.Application;

public interface UserContextService {

    String getRtxId();

    String getIp();

    void setIp(String ip);

    Application getApplication(String group);
}
