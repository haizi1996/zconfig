package com.hailin.admin.dao;

import com.hailin.admin.entity.FileContentMD5;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileContentMD5Dao {

    void insert(FileContentMD5 fileContentMD5);
}
