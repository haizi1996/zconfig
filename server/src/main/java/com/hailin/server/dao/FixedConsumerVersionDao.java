package com.hailin.server.dao;

import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.KeyValuePair;
import com.hailin.server.common.bean.MetaIp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FixedConsumerVersionDao {

    Long find(@Param("meta") ConfigMeta meta, @Param("ip")String ip);

    List<KeyValuePair<MetaIp, Long>> queryAll();
}
