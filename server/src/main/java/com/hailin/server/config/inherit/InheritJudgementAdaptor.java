package com.hailin.server.config.inherit;

import com.google.common.base.Optional;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.service.ConfigInfoService;

public class InheritJudgementAdaptor implements InheritJudgement {


    public static InheritJudgement create(ConfigInfoService configInfoService) {
        return new InheritJudgementAdaptor(configInfoService);
    }

    private ConfigInfoService configInfoService;

    private InheritJudgementAdaptor(ConfigInfoService configInfoService) {
        this.configInfoService = configInfoService;
    }

    @Override
    public boolean exist(InheritMeta meta) {
        Optional<ConfigMeta> parent = configInfoService.getParent(meta.getChild());
        return parent.isPresent() && parent.get().equals(meta.getParent());
    }

}
