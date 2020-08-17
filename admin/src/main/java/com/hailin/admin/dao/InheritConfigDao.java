package com.hailin.admin.dao;


import com.hailin.admin.model.Reference;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InheritConfigDao {

    void save( Reference reference);
}
