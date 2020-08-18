package com.hailin.server.config.zfile.impl;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.config.Config;
import com.hailin.server.config.inherit.InheritMeta;
import com.hailin.server.config.inherit.InheritUtil;
import com.hailin.server.config.zfile.ZFile;
import com.hailin.server.config.zfile.ZFileFactory;
import com.hailin.server.service.ConfigInfoService;
import com.hailin.server.support.context.ClientInfoService;

import javax.annotation.Resource;

public abstract class AbstractZFileFactory implements ZFileFactory {

    @Resource
    private ClientInfoService clientInfoService;

    protected ClientInfoService getClientInfoService() {
        return clientInfoService;
    }

    @Override
    public Optional<ZFile> create(ConfigMeta meta, ConfigInfoService configInfoService) {
        // 引用了外部系统的配置
        if (!Objects.equal(meta.getGroup() , clientInfoService.getGroup())){
            ConfigMeta childRequestMeta = new ConfigMeta(clientInfoService.getGroup() , meta.getDataId() , meta.getProfile());
            InheritMeta fuzzyInheritMeta = InheritMeta.builder().parent(meta).child(childRequestMeta).build();
            Optional<InheritMeta> inheritMeta = InheritUtil.getInheritRelationWithFuzzyRelation(fuzzyInheritMeta, InheritJudgementAdaptor.create(configInfoService), clientInfoService.getRoom());


        }


        return null;
    }
}
