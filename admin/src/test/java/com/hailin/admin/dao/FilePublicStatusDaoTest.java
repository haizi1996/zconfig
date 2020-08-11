package com.hailin.admin.dao;

import com.hailin.server.common.bean.ConfigMetaWithoutProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FilePublicStatusDaoTest {

    @Resource
    private FilePublicStatusDao filePublicStatusDao;

    @Test
    public void getPublicType() {
        Integer publicStatusCode = filePublicStatusDao.getPublicType(new ConfigMetaWithoutProfile("test" , "test.txt"));

        System.out.println(publicStatusCode);
    }
}