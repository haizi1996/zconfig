package com.hailin.admin.service.impl;

import com.hailin.admin.service.ReferenceService;
import com.hailin.server.common.bean.ConfigMeta;

public class ReferenceServiceDecorator implements ReferenceService {

    private ReferenceService delegate;

    public ReferenceServiceDecorator(ReferenceService delegate) {
        this.delegate = delegate;
    }
    @Override
    public int beReferenceCount(ConfigMeta meta) {
        return delegate.beReferenceCount(meta);
    }
}
