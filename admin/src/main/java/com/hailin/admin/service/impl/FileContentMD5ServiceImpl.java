package com.hailin.admin.service.impl;

import com.hailin.admin.entity.FileContentMD5;
import com.hailin.admin.dao.FileContentMD5Dao;
import com.hailin.admin.service.FileContentMD5Service;
import com.hailin.server.common.bean.CandidateSnapshot;
import com.hailin.zconfig.common.util.ChecksumAlgorithm;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FileContentMD5ServiceImpl implements FileContentMD5Service {

    @Resource
    private FileContentMD5Dao fileContentMD5Dao;

    @Override
    public void applyConfigChange(CandidateSnapshot snapshot) {

        FileContentMD5 fileContentMD5 = new FileContentMD5(snapshot.getGroup(), snapshot.getProfile(),
                snapshot.getDataId(), (int) snapshot.getEditVersion(),
                ChecksumAlgorithm.getChecksum(snapshot.getData()));
        fileContentMD5Dao.insert(fileContentMD5);
    }
}
