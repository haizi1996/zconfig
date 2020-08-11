package com.hailin.admin.service;

import com.hailin.server.common.bean.ConfigMeta;

public interface ReferenceService {

    /**
     * 统计文件的引用次数
     */
    int beReferenceCount(ConfigMeta meta);
}
