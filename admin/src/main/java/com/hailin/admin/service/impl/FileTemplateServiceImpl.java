package com.hailin.admin.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hailin.admin.dao.DefaultTemplateConfigMappingDao;
import com.hailin.admin.dao.FileTemplateDao;
import com.hailin.admin.dao.FileTemplateMappingDao;
import com.hailin.admin.dao.FileTemplateMappingVersionDao;
import com.hailin.admin.entity.TemplateInfo;
import com.hailin.admin.model.KeyValuePair;
import com.hailin.admin.model.TemplateType;
import com.hailin.admin.service.FileTemplateService;
import com.hailin.server.common.bean.ConfigMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FileTemplateServiceImpl implements FileTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(FileTemplateServiceImpl.class);

    private static final int MAX_TEMPLATE_DESC_LENGTH = 150;
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 50;
    private static final int LOWEST_VERSION = 0;
    private static final int ERROR_NUMBER = -1;

    @Resource
    private FileTemplateDao templateDao;

    @Resource
    private FileTemplateMappingDao templateMappingDao;

    @Resource
    private FileTemplateMappingVersionDao fileTemplateMappingVersionDao;

    @Resource
    private DefaultTemplateConfigMappingDao defaultTemplateConfigMappingDao;

    @Override
    public Optional<KeyValuePair<String, String>> getTemplate(String group, String dataId) {


        Preconditions.checkArgument(!Strings.isNullOrEmpty(group), "group can note be empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dataId), "dataId can not be empty");

        List<KeyValuePair<String, String>> entry = fileTemplateMappingVersionDao.selectTemplate(group, dataId);
        if (CollectionUtils.isEmpty(entry)){
            entry = templateMappingDao.selectTemplate(group, dataId);
        }
        return Optional.ofNullable(entry.get(0));
    }

    @Override
    public Optional<TemplateInfo> getTemplateInfo(String group, String template) {
        try {
            TemplateInfo templateInfo = templateDao.selectTemplateInfo(group, template);
            dealNewJsonTemplate(templateInfo);
            return Optional.ofNullable(templateInfo);
        } catch (EmptyResultDataAccessException ignore) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteDefaultConfigId(ConfigMeta configMeta) {
        defaultTemplateConfigMappingDao.delete(configMeta);
    }

    @Override
    public Optional<String> getTemplateDetailByFile(String group, String dataId) {
        return Optional.empty();
    }

    private Optional<String> getLimitNameFormDetail(String templateDetail) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode node = objectMapper.readTree(templateDetail);
            JsonNode nameNode = node.get("fileName");
            if (nameNode != null && !nameNode.isNull()) {
                return Optional.of(nameNode.asText());
            }
        } catch (IOException e) {
            LOG.error("校验反序列化失败");
        }
        return Optional.empty();
    }

    private void dealNewJsonTemplate(TemplateInfo templateInfo) {
        if (Objects.isNull(templateInfo) || !Objects.equals(templateInfo.getType() , TemplateType.JSON_SCHEMA)){
            return;
        }
        String detail = templateInfo.getDetail();
        String templateDetail = getTemplateDetailFormDetail(detail);
        Optional<String> limitName = getLimitNameFormDetail(detail);
        if (!Strings.isNullOrEmpty(templateDetail) && limitName.isPresent()) {
            templateInfo.setDetail(templateDetail);
        }
    }
    /**
     * 从新版本的detail中获取具体的templateDetail
     *
     * @param detail 新版本detail
     * @return templateDetail
     *
     */
    private String getTemplateDetailFormDetail(String detail) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode node = objectMapper.readTree(detail);
            JsonNode nameNode = node.get("templateDetail");
            if (nameNode == null || nameNode.isNull()) {
                return "";
            } else {
                return nameNode.asText();
            }
        } catch (IOException e) {
            LOG.error("校验反序列化失败");
        }
        return "";
    }
}
