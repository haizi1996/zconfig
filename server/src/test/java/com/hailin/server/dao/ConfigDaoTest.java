package com.hailin.server.dao;


import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ConfigDaoTest {

    @Resource
    private ConfigDao configDao;

    @Test
    public void loadAll(){
        List<VersionData<ConfigMeta>>  datas =  configDao.loadAll();
        System.out.println(datas);
    }

}