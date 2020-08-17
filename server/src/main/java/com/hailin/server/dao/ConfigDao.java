package com.hailin.server.dao;

import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;

import java.util.List;

@Mapper
public interface ConfigDao {

    List<VersionData<ConfigMeta>> loadAll();
}
