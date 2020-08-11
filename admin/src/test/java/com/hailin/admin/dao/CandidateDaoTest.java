package com.hailin.admin.dao;

import com.hailin.server.common.bean.Candidate;
import com.hailin.zconfig.common.bean.StatusType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CandidateDaoTest {

    @Resource
    private CandidateDao candidateDao;

    @Test
    public void update() {
        StatusType statusType = StatusType.PENDING;
        Candidate candidate = new Candidate("qconfig", "test.properties", "prod:",
                0, 2, StatusType.DELETE);
        int result = candidateDao.update(candidate , statusType);
        System.out.println(result);
    }
}