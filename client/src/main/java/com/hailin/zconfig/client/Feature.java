package com.hailin.zconfig.client;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Feature {

    public static final Feature DEFAULT = Feature.builder().build();

    // 是否自动载入
    private final boolean autoReload;

    // 能容忍的最低版本限度
    private final long minimumVersion;

    // 允许载入不存在的文件, 当文件存在时自动生效.
    private final boolean failOnNotExists;

    private final boolean trimValue;

    //是否采用https访问，默认关闭
    private final boolean httpsEnable;

}
