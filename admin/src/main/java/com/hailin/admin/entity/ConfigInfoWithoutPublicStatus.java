package com.hailin.admin.entity;

import com.hailin.admin.model.InheritConfigMeta;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class ConfigInfoWithoutPublicStatus {

    private String group;
    private String dataId;
    private String profile;
    private long version;
    private boolean inuse;

    private InheritConfigMeta inheritConfigMeta;

    private Timestamp updateTime;

    public ConfigInfoWithoutPublicStatus(String group, String dataId, String profile, long version, boolean publish, Timestamp updateTime) {
        this.group = group;
        this.dataId = dataId;
        this.profile = profile;
        this.version = version;
        this.inuse = publish;
        this.updateTime = updateTime;
    }
}
