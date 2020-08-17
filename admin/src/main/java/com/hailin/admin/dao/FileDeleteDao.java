package com.hailin.admin.dao;

import com.hailin.server.common.bean.ConfigMeta;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileDeleteDao {

    int insert(ConfigMeta meta, List<String> ips);

    int exist(ConfigMeta meta);
}
