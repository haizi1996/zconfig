package com.hailin.admin.dto;

import com.hailin.zconfig.common.bean.StatusType;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import sun.jvm.hotspot.debugger.cdbg.RefType;

@Data
public class CandidateDTO {

    private String uuid = StringUtils.EMPTY;

    private String group;

    private String dataId;

    private String profile;

    private long basedVersion = 0;

    private long editVersion = 0;

    private StatusType status;

    private String description;

    private String templateGroup;

    private String template;

    private int templateVersion;

    private String data;

    private String jsonDiff;

    private String validateUrl;

    private long defaultConfigId = 0;

    private String inheritGroupId;

    private String inheritDataId;

    private String inheritProfile;

    private String inheritData;

    private String templateDetail;

    private boolean sendMail = false;

    private boolean isForceUpload;

    public RefType refType;

    private String message;

    private String comment;

    public CandidateDTO() {

    }

    public CandidateDTO(String group, String dataId, String profile, String data) {
        this.group = group;
        this.dataId = dataId;
        this.profile = profile;
        this.data = data;
    }

    public CandidateDTO(String group, String dataId, String profile,
                        long basedVersion, long editVersion, StatusType status,
                        String description, String templateGroup, String template,
                        String data, String jsonDiff, String validateUrl, long defaultConfigId,
                        String inheritGroupId, String inheritDataId, String inheritProfile, String inheritData) {

        this.group = group;
        this.dataId = dataId;
        this.profile = profile;
        this.basedVersion = basedVersion;
        this.editVersion = editVersion;
        this.status = status;
        this.description = description;
        this.templateGroup = templateGroup;
        this.template = template;
        this.data = data;
        this.jsonDiff = jsonDiff;
        this.validateUrl = validateUrl;
        this.defaultConfigId = defaultConfigId;
        this.inheritGroupId = inheritGroupId;
        this.inheritDataId = inheritDataId;
        this.inheritProfile = inheritProfile;
        this.inheritData = inheritData;
    }

    public CandidateDTO(String group, String dataId, String profile, long basedVersion, long editVersion, StatusType status, String data) {
        this.group = group;
        this.dataId = dataId;
        this.profile = profile;
        this.basedVersion = basedVersion;
        this.editVersion = editVersion;
        this.status = status;
        this.data = data;
    }

    public CandidateDTO copy() {
        CandidateDTO dto = new CandidateDTO();
        dto.uuid = this.getUuid();
        dto.group = this.getGroup();
        dto.dataId = this.getDataId();
        dto.profile = this.getProfile();
        dto.basedVersion = this.getBasedVersion();
        dto.editVersion = this.getEditVersion();
        dto.status = this.getStatus();
        dto.data = this.getData();
        dto.template = this.getTemplate();
        dto.templateGroup = this.templateGroup;
        dto.templateVersion = this.templateVersion;
        dto.validateUrl = this.validateUrl;
        dto.defaultConfigId = this.defaultConfigId;
        dto.templateDetail = templateDetail;
        dto.inheritDataId = this.inheritDataId;
        dto.inheritGroupId = this.inheritGroupId;
        dto.inheritProfile = this.inheritProfile;
        dto.inheritData = this.inheritData;
        dto.sendMail = this.sendMail;
        dto.refType = this.refType;
        dto.message = this.message;
        dto.comment = this.comment;
        return dto;
    }
}
