package com.hailin.server.dao;

import com.hailin.server.common.bean.KeyValuePair;
import com.hailin.server.common.bean.MetaIp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FixedConsumerVersionDaoTest {

    @Resource
    private FixedConsumerVersionDao fixedConsumerVersionDao;

    @Test
    public void find() {
    }

    @Test
    public void queryAll() {
        List<KeyValuePair<MetaIp, Long>> res = fixedConsumerVersionDao.queryAll();
        System.out.println(res);
    }
}