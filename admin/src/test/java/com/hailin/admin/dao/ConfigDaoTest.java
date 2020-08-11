package com.hailin.admin.dao;

import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ConfigDaoTest {

    @Resource
    private ConfigDao configDao;

    @Test
    public void delete() {

        ConfigMeta configMeta = new ConfigMeta("qconfig", "push_mail_switch.properties", "dev:");
        int deleted = configDao.delete(
                VersionData.of(5, configMeta),
                4);
        System.out.println(deleted);
    }
}