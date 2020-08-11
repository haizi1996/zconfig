package com.hailin.admin.service.impl;

import com.hailin.admin.dao.FilePublicStatusDao;
import com.hailin.admin.service.FilePublicStatusService;
import com.hailin.admin.support.CheckUtil;
import com.hailin.server.common.bean.ConfigMetaWithoutProfile;
import com.hailin.zconfig.common.util.PublicType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FilePublicStatusServiceImpl implements FilePublicStatusService {

    @Resource
    private FilePublicStatusDao filePublicStatusDao;

    @Override
    public boolean isPublic(ConfigMetaWithoutProfile configMetaWithoutProfile) {
        CheckUtil.checkLegalGroup(configMetaWithoutProfile.getGroup());
        CheckUtil.checkLegalDataId(configMetaWithoutProfile.getDataId());
        Integer publicTypeCode = filePublicStatusDao.getPublicType(configMetaWithoutProfile);
        if (publicTypeCode == null) {
            return false;
        }
        return new PublicType(publicTypeCode).isPublic();
    }
}
