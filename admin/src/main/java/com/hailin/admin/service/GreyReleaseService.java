package com.hailin.admin.service;

import com.hailin.server.common.bean.ConfigMeta;

public interface GreyReleaseService {

    boolean insertTaskMapping(ConfigMeta meta, String uuid);

    boolean deleteTaskMapping(ConfigMeta meta, String uuid);
}
