package com.hailin.admin.dao;

import com.hailin.server.common.bean.ConfigMeta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SnapshotDao {

    void save(@Param("editVersion") long editVersion, @Param("meta")ConfigMeta configMeta, @Param("checksum")String checksum, @Param("content") String content ,  @Param("basedVersion")long basedVersion);
}
