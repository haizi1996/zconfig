package com.hailin.server.domain;

import com.google.common.collect.ImmutableList;
import com.hailin.server.common.bean.ConfigMeta;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Changed {

    private String group;
    private String dataId;
    private String profile;
    private long newestVersion;

    private volatile String str = null;

    public Changed(ConfigMeta meta, long newestVersion) {
        this.group = meta.getGroup();
        this.dataId = meta.getDataId();
        this.profile = meta.getProfile();
        this.newestVersion = newestVersion;
    }

    public Changed(String group, String dataId, String profile, long newestVersion) {
        this.group = group;
        this.dataId = dataId;
        this.profile = profile;
        this.newestVersion = newestVersion;
    }

    public String str() {
        if (str == null) {
            str = AbstractCheckConfigServlet.formatOutput(ImmutableList.<Changed>of(this));
        }
        return str;
    }

}
