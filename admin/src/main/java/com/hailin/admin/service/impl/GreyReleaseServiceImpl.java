package com.hailin.admin.service.impl;

import com.hailin.admin.dao.BatchPushTaskMappingDao;
import com.hailin.admin.service.GreyReleaseService;
import com.hailin.server.common.bean.ConfigMeta;
import org.springframework.stereotype.Service;

@Service
public class GreyReleaseServiceImpl implements GreyReleaseService {

    private BatchPushTaskMappingDao batchPushTaskMappingDao;

    @Override
    public boolean insertTaskMapping(ConfigMeta meta, String uuid) {
        return batchPushTaskMappingDao.insert(meta, uuid) > 0 ;
    }

    @Override
    public boolean deleteTaskMapping(ConfigMeta meta, String uuid) {
        return batchPushTaskMappingDao.delete(meta , uuid) > 0 ;
    }
}
