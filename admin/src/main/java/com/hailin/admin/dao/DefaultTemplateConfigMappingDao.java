package com.hailin.admin.dao;

import com.hailin.server.common.bean.ConfigMeta;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DefaultTemplateConfigMappingDao {

    void delete(ConfigMeta configMeta);
}
