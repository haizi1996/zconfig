package com.hailin.admin.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileContentMD5 {

    private String group;

    private String profile;

    private String dataId;

    private int version;

    private String md5;

    public FileContentMD5() {
    }

    public FileContentMD5(String group, String profile, String dataId, int version, String md5) {
        this.group = group;
        this.profile = profile;
        this.dataId = dataId;
        this.version = version;
        this.md5 = md5;
    }
}
