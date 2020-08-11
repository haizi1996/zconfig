package com.hailin.admin.cloud.controller;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.entity.TemplateInfo;
import com.hailin.admin.model.TemplateType;
import com.hailin.admin.service.EventPostApplyService;
import com.hailin.admin.service.FileTemplateService;
import com.hailin.admin.service.ProfileService;
import com.hailin.admin.support.AdminConstants;
import com.hailin.admin.web.controller.AbstractControllerHelper;
import com.hailin.server.common.util.Environment;
import com.hailin.server.common.util.ProfileUtil;
import com.hailin.zconfig.common.bean.JsonV2;
import com.hailin.zconfig.common.util.FileChecker;
import com.hailin.zconfig.common.util.ZConfigAttributesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;


@RestController
@RequestMapping("/zconfig/file")
public class ActionController extends AbstractControllerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionController.class);

    @Resource
    private EventPostApplyService applyService;

    @Resource
    private ProfileService profileService;

    @Resource
    private FileTemplateService templateService;

    /**
     *  创建文件
     */
    @PostMapping("/apply")
    public Object apply(@RequestBody CandidateDTO candidate,
                        @RequestParam(value = "isForceApply", required = false, defaultValue = "false") boolean isForceApply){
        checkApply(candidate);

        try {
            if (isForceApply){
                applyService.forceApply(candidate , "");
            }else {
                applyService.apply(candidate , "");
            }
            return JsonV2.success();
        }catch (Exception e){
            return handleException(candidate , e);
        }
    }



    private void checkApply(CandidateDTO candidate) {
        checkCandidate(candidate);
        String buildGroup = ProfileUtil.getBuildGroup(candidate.getProfile());
        if (!Strings.isNullOrEmpty(buildGroup)) {
            checkArgument(profileService.exist(candidate.getGroup(), candidate.getProfile()),
                    "group [%s] 没有profile [%s]", candidate.getGroup(), candidate.getProfile());
        }

        checkArgument(!Strings.isNullOrEmpty(candidate.getData() + " "), "文件内容不能为空");

        checkTemplate(candidate);
    }

    private void checkCandidate(CandidateDTO candidate) {
        checkLegalMeta(candidate);
        checkArgument(candidate.getBasedVersion() >= 0, "无效的 based version");
        checkLegalEditVersion(candidate.getEditVersion());

        FileChecker.checkName(candidate.getDataId());

        String buildGroup = ProfileUtil.getBuildGroup(candidate.getProfile());
        checkArgument(buildGroup.length() <= Environment.BUILD_GROUP_MAX_LENGTH, "%s长度不能大于%s个字符", ZConfigAttributesLoader.getInstance().getBuildGroup(), Environment.BUILD_GROUP_MAX_LENGTH);
        checkArgument(ProfileUtil.BUILD_GROUP_LETTER_DIGIT_PATTERN.matcher(buildGroup).find(), "%s不能包含[字符，数字，'_'，'-']以外的其它内容", ZConfigAttributesLoader.getInstance().getBuildGroup());

        // todo: 好几处地方有长度限制了，得弄一下
        checkArgument(candidate.getData() == null || candidate.getData().getBytes(Charsets.UTF_8).length < AdminConstants.MAX_FILE_SIZE_IN_K * 1024, "文件大小不能超过%sk", AdminConstants.MAX_FILE_SIZE_IN_K);

        checkArgument(candidate.getDescription() == null || candidate.getDescription().length() <= AdminConstants.MAX_DESC_LENGTH, "文件描述长度不能超过%s个字", AdminConstants.MAX_DESC_LENGTH);
    }


    private void checkTemplate(CandidateDTO candidate) {
        if (FileChecker.isTemplateFile(candidate.getDataId())) {
            checkArgument(!Strings.isNullOrEmpty(candidate.getTemplateGroup()) && !Strings.isNullOrEmpty(candidate.getTemplate()), ".t后缀代表模版文件，必须采用模版");
        }

        if (!Strings.isNullOrEmpty(candidate.getTemplateGroup()) && !Strings.isNullOrEmpty(candidate.getTemplate())) {
            final Optional<TemplateInfo> info = templateService.getTemplateInfo(candidate.getTemplateGroup(), candidate.getTemplate());
            if (info.isPresent()) {
                final TemplateType type = info.get().getType();
                if (type == TemplateType.JSON_SCHEMA) {
                    checkArgument(candidate.getDataId().endsWith(".json"), "使用了JSON模板，文件后缀必须是.json");
                } else if (type == TemplateType.TABLE) {
                    checkArgument(candidate.getDataId().endsWith(".t"), "使用了表格模板，文件后缀必须是.t");
                }
            }
        }
    }
}
