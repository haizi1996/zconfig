package com.hailin.admin.dao;

import com.hailin.admin.model.KeyValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
public class FileTemplateMappingDaoTest {

    @Resource
    private FileTemplateMappingDao fileTemplateMappingDao;

    @Test
    public void selectTemplate() {

        List<KeyValuePair<String , String>> pair =  fileTemplateMappingDao.selectTemplate("qconfig" , "qconfig_file_type.t");
        System.out.println(pair);
    }
}