package com.hailin.admin.dao;

import com.hailin.admin.entity.ConfigInfoWithoutPublicStatus;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ConfigDao {

    int delete(@Param("configId") VersionData<ConfigMeta> configId, @Param("oldVersion") long oldVersion);

    ConfigInfoWithoutPublicStatus findCurrentConfigInfo(ConfigMeta configMeta);
}
