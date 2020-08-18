package com.hailin.server.config.inherit;

import com.hailin.server.common.bean.ConfigMeta;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InheritMeta {

    private final ConfigMeta parent;

    private final ConfigMeta child;

    InheritMeta(ConfigMeta parent, ConfigMeta child) {
        this.parent = parent;
        this.child = child;
    }
}
