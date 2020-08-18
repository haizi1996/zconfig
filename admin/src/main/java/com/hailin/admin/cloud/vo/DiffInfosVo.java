package com.hailin.admin.cloud.vo;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.hailin.admin.model.ConfigOpLog;
import com.hailin.server.common.bean.KeyValuePair;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DiffInfosVo {

    private String group;

    private String dataId;

    private String profile;

    private String data;

    private JsonNode dataObject;

    private String templateDetail;

    private List<KeyValuePair<ConfigMetaVersion, Object>> diffs;

    private List<ConfigOpLog> oplogs;

    private boolean intercept = false;

    private boolean showDiffAlert = false;

    private String diffAlertText = "";

    public <T> DiffInfosVo(ConfigMeta meta, String data, JsonNode dataObject, String templateDetail, List<Map.Entry<VersionData<ConfigMeta>, T>> diffs, List<ConfigOpLog> oplogs) {
        this.group = meta.getGroup();
        this.dataId = meta.getDataId();
        this.profile = meta.getProfile();
        this.data = data;
        this.dataObject = dataObject;
        this.templateDetail = templateDetail;
        this.diffs = Lists.newArrayListWithCapacity(diffs.size());
        for (Map.Entry<VersionData<ConfigMeta>, T> diff : diffs) {
            this.diffs.add(new KeyValuePair<>(new ConfigMetaVersion(diff.getKey()), (Object)diff.getValue()));
        }
        this.oplogs = oplogs;
    }

}
