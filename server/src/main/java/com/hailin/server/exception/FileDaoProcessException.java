package com.hailin.server.exception;

import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;

public class FileDaoProcessException extends Exception {
    private static final long serialVersionUID = 1771137937219205379L;
    private final VersionData<ConfigMeta> configId;

    public FileDaoProcessException(VersionData<ConfigMeta> configId, String message) {
        super(message);
        this.configId = configId;
    }

    public VersionData<ConfigMeta> getConfigId() {
        return configId;
    }
}
