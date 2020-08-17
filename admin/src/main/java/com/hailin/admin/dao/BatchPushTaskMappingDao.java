package com.hailin.admin.dao;

import com.hailin.server.common.bean.ConfigMeta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BatchPushTaskMappingDao {

    int insert(@Param("meta") ConfigMeta meta,@Param("uuid") String uuid);

    int delete(ConfigMeta meta, String uuid);
}
