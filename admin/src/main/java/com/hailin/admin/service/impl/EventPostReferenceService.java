package com.hailin.admin.service.impl;

import com.hailin.admin.service.ReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class EventPostReferenceService extends ReferenceServiceDecorator {

    @Autowired
    public EventPostReferenceService(@Qualifier("referenceService") ReferenceService delegate) {
        super(delegate);
    }
}
