package com.hailin.server.vo;

import com.hailin.server.common.bean.ConfigMeta;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetConfigMeta extends ConfigMeta {

    private long version;

}
