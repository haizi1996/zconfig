package com.hailin.zconfig.client.impl;

import com.hailin.zconfig.client.Configuration;
import com.hailin.zconfig.client.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractConfiguration<T> implements Configuration<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractConfiguration.class);

    protected final Feature feature;

    protected final String fileName;

    protected static final String STUB_FILE_NAME = "stub";

    private static final int MAX_ALLOWED_LISTENERS = 50;

    private final AtomicInteger listenerCount = new AtomicInteger();

    public AbstractConfiguration(Feature feature) {
        this(feature, STUB_FILE_NAME);
    }

    public AbstractConfiguration(Feature feature, String fileName) {
        this.feature = feature;
        this.fileName = fileName;
    }
}
