package com.hailin.admin.dao;

import com.hailin.admin.model.EncryptKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EncryptKeyDao {

    List<EncryptKey> select(@Param("group") String group, @Param("dataId") String dataId);
}
