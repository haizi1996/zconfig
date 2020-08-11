package com.hailin.admin.service;

import com.hailin.admin.entity.TemplateInfo;
import com.hailin.admin.model.KeyValuePair;
import com.hailin.server.common.bean.ConfigMeta;

import java.util.Optional;

public interface FileTemplateService {

    Optional<KeyValuePair<String, String>> getTemplate(String group, String dataId);

    Optional<TemplateInfo> getTemplateInfo(String group, String template);

    void deleteDefaultConfigId(ConfigMeta configMeta);

    Optional<String> getTemplateDetailByFile(String group, String dataId);
}
