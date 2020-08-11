package com.hailin.admin.dao;

import com.hailin.admin.model.KeyValuePair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileTemplateMappingVersionDao {

    List<KeyValuePair<String, String>> selectTemplate(@Param("group") String group, @Param("dataId")String dataId);

}
