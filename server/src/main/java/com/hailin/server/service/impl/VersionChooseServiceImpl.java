package com.hailin.server.service.impl;

import com.hailin.server.config.zfile.ZFileFactory;
import com.hailin.server.service.VersionChooseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class VersionChooseServiceImpl implements VersionChooseService {

    @Resource(name = "v2Factory")
    private ZFileFactory qFileFactory;

    @Override
    public ZFileFactory getQFileFactory() {
        return qFileFactory;
    }
}
