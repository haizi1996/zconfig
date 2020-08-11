package com.hailin.admin.dao;

import com.hailin.admin.entity.TemplateInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileTemplateDao {

    TemplateInfo selectTemplateInfo(@Param("group")String group, @Param("template")String template);
}
