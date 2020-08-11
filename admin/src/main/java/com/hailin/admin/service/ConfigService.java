package com.hailin.admin.service;

import com.hailin.admin.entity.ConfigInfoWithoutPublicStatus;
import com.hailin.admin.exception.ModifiedException;
import com.hailin.server.common.bean.CandidateSnapshot;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;

public interface ConfigService {

    ConfigInfoWithoutPublicStatus findPublishedConfigWithoutPublicStatus(ConfigMeta configMeta);

    void delete(CandidateSnapshot snapshot) throws ModifiedException;

    VersionData<String> getCurrentPublishedData(ConfigMeta meta);

    String templateDataLongToStr(String group, String dataId, String data);
}
