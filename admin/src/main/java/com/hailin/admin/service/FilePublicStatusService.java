package com.hailin.admin.service;

import com.hailin.server.common.bean.ConfigMetaWithoutProfile;

public interface FilePublicStatusService {
    /**
     * 判断文件是否是public
     */
    boolean isPublic(ConfigMetaWithoutProfile configMetaWithoutProfile);
}
