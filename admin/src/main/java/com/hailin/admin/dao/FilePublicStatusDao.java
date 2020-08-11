package com.hailin.admin.dao;

import com.hailin.server.common.bean.ConfigMetaWithoutProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FilePublicStatusDao {
    /**
     * 查询文件的公开状态
     */
    Integer getPublicType(ConfigMetaWithoutProfile configMetaWithoutProfile);

}
