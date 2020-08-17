package com.hailin.admin.service;

import com.hailin.server.common.bean.ConfigMeta;

public interface FileCommentService {

    void setComment(ConfigMeta meta, long version, String comment);
}
