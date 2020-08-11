package com.hailin.admin.service.impl;

import com.hailin.admin.dao.ReferenceDao;
import com.hailin.admin.service.ReferenceService;
import com.hailin.server.common.bean.ConfigMeta;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("referenceService")
public class ReferenceServiceImpl implements ReferenceService {

    @Resource
    private ReferenceDao referenceDao;

    @Override
    public int beReferenceCount(ConfigMeta meta) {
        return referenceDao.referenceCount(meta);
    }
}
