package com.hailin.admin.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class InheritConfigMeta {

    //应用名称
    private String groupId;

    //文件名称
    private String dataId;

    //文件环境
    private String profile;

    private String inheritGroupId;

    private String inheritDataId;

    private String inheritProfile;

    private String operator;

    private int status;

    private Date createTime;

    public InheritConfigMeta() {

    }

    public InheritConfigMeta(String groupId, String dataId, String profile) {
        this.groupId = groupId;
        this.dataId = dataId;
        this.profile = profile;
    }

    public InheritConfigMeta(String groupId, String dataId) {
        this.groupId = groupId;
        this.dataId = dataId;
    }
}
