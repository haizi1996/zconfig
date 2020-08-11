package com.hailin.admin.dao;

import com.hailin.admin.model.ConfigOpLog;
import com.hailin.server.common.bean.ConfigMeta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConfigOpLogDao {

    List<ConfigOpLog> selectRecent(@Param("meta") ConfigMeta meta, @Param("baseVersion") long baseVersion, @Param("length") int configLogLength);
}
