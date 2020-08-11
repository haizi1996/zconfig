package com.hailin.admin.web.controller;

import com.google.common.io.ByteStreams;
import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.exception.ConfigExistException;
import com.hailin.admin.exception.FileDeletingException;
import com.hailin.admin.exception.IllegalTemplateException;
import com.hailin.admin.exception.ModificationDuringPublishingException;
import com.hailin.admin.exception.ModifiedException;
import com.hailin.admin.exception.OnePersonPublishException;
import com.hailin.admin.exception.PropertiesConflictException;
import com.hailin.admin.exception.StatusMismatchException;
import com.hailin.admin.exception.TemplateChangedException;
import com.hailin.admin.exception.TemplateNameNotMatchException;
import com.hailin.admin.exception.ValidateErrorException;
import com.hailin.admin.exception.ValidateMessageException;
import com.hailin.admin.exception.ValueCheckException;
import com.hailin.admin.model.Conflict;
import com.hailin.admin.support.CheckUtil;
import com.hailin.server.common.bean.Candidate;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.zconfig.common.bean.JsonV2;
import com.hailin.zconfig.common.bean.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

public class AbstractControllerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractControllerHelper.class);

    protected static final long INIT_BASED_VERSION = 0;

    protected static final int MAX_USERNAME_LENGTH = 50;

    protected void checkLegalMeta(CandidateDTO candidate) {
        checkLegalMeta(new ConfigMeta(candidate.getGroup(), candidate.getDataId(), candidate.getProfile()));
    }

    protected void checkLegalMeta(String group, String profile, String dataId) {
        checkLegalMeta(new ConfigMeta(group, dataId, profile));
    }

    protected void checkLegalMeta(ConfigMeta meta) {
        checkLegalGroup(meta.getGroup());
        checkLegalProfile(meta.getProfile());
        checkLegalDataId(meta.getDataId());
    }

    protected void checkLegalEditVersion(long version) {
        checkArgument(version >= 0, "无效的 edit version");
    }

    protected void checkLegalDataId(String dataId) {
        CheckUtil.checkLegalDataId(dataId);
    }

    protected void checkLegalProfile(String profile) {
        CheckUtil.checkLegalProfile(profile);
    }

    protected void checkLegalGroup(String group) {
        CheckUtil.checkLegalGroup(group);
    }

    protected void checkLegalRtxId(String rtxId) {
        checkArgument(rtxId.length() > 0, "名称过短");
        checkArgument(rtxId.length() < MAX_USERNAME_LENGTH, "名称过长");
        for (char c : rtxId.toCharArray()) {
            if (c >= 'a' && c <= 'z')
                continue;
            if (c >= 'A' && c <= 'Z')
                continue;
            if (c == '.')
                continue;
            if (c == '_')
                continue;
            if (Character.isDigit(c))
                continue;
            throw new IllegalArgumentException("name[" + rtxId + "]包含非法字符");
        }
    }

    protected String getFileData(final MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            return Arrays.toString(ByteStreams.toByteArray(stream));
        } catch (IOException e) {
            LOGGER.error("parse file {} data error", file.getName(), e);
            throw new RuntimeException("解析文件发生错误", e);
        }
    }

    protected Object handleException(CandidateDTO candidateDTO, Exception e) {
        if (e instanceof ModifiedException) {
            LOGGER.info("config modified, {}", candidateDTO);
            return errorJson("该记录状态已被修改过，执行操作失败！");
        } else if (e instanceof StatusMismatchException) {
            LOGGER.info("config status wrong, {}", candidateDTO);
            return errorJson("该记录状态不允许执行该操作！");
        } else if (e instanceof ModificationDuringPublishingException) {
            LOGGER.info("modification during publishing, {}", candidateDTO);
            return errorJson("该文件正在发布中，执行操作失败！");
        } else if (e instanceof ConfigExistException) {
            Conflict conflict = ((ConfigExistException) e).getConflict();
            if (isDeleteConflict(conflict)) {
                LOGGER.info("config deleted, {}", candidateDTO);
                return new JsonV2<Candidate>(ResultStatus.CONFIG_ALREADY_DELETED.code(), "该文件已被删除", conflict.getCandidate());
            } else {
                LOGGER.info("config exist, {}", candidateDTO);
                if (conflict.getType() == Conflict.Type.EXIST) {
                    return errorJson("该文件名已存在");
                } else {
                    return errorJson("该文件名已被引用或取消引用");
                }
            }
        } else if (e instanceof TemplateChangedException) {
            return errorJson("文件所属模版不能更改");
        } else if (e instanceof IllegalTemplateException) {
            return errorJson("模版[" + ((IllegalTemplateException) e).getTemplate() + "]不存在");
        } else if (e instanceof ValueCheckException) {
            return errorJson(e.getMessage());
        } else if (e instanceof PropertiesConflictException) {
            return errorJson("文件内存在重复的key: [" + ((PropertiesConflictException) e).getKey() + "] 等");
        } else if (e instanceof ValidateMessageException) {
            return errorJson(e.getMessage());
        } else if (e instanceof ValidateErrorException) {
            return new JsonV2<>(ResultStatus.VALIDATE_ERROR.code(), "访问应用方校验接口未通过", ((ValidateErrorException) e).getError());
        } else if (e instanceof OnePersonPublishException) {
            return errorJson("线上环境文件不能只由一人编辑审核并发布，至少要有两个人参与");
        } else if (e instanceof FileDeletingException) {
            return errorJson("文件在等待server删除缓存，请联系管理员");
        } else if (e instanceof IllegalArgumentException) {
            return errorJson(e.getMessage());
        } else if (e instanceof TemplateNameNotMatchException) {
            return errorJson("文件与模版文件名限制不匹配");
        } else {
            LOGGER.error("config error with {}", candidateDTO, e);
            return errorJson("系统发生异常，请与管理员联系！");
        }
    }

    private boolean isDeleteConflict(Conflict conflict) {
        return conflict.getType() == Conflict.Type.EXIST && conflict.getCandidate().getStatus() == StatusType.DELETE;
    }


    protected enum ResultStatus {
        SUCCESS(0),
        NORMAL_ERROR(2),
        ILLEGAL_FILE(5),
        CONFIG_ALREADY_DELETED(20),
        VALIDATE_ERROR(100);

        private final int code;

        ResultStatus(final int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }
    }

    protected enum PageStatus {
        CAN_EDIT, CAN_LOAD, FIXED
    }


    private Object errorJson(String message) {
        return new JsonV2<>(-1, message, null);
    }

}
