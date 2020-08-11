package com.hailin.admin.cloud.controller;

import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.service.EventPostApplyService;
import com.hailin.admin.service.FilePublicStatusService;
import com.hailin.admin.service.ReferenceService;
import com.hailin.admin.web.controller.AbstractControllerHelper;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.ConfigMetaWithoutProfile;
import com.hailin.zconfig.common.bean.JsonV2;
import com.hailin.zconfig.common.bean.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/zconfig/file")
public class FileController extends AbstractControllerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @Resource
    private FilePublicStatusService filePublicStatusService;

    @Resource
    private ReferenceService eventPostReferenceService;

    @Resource
    private EventPostApplyService applyService;

    @PostMapping("/delete")
    public Object delete(@RequestBody CandidateDTO dto){

        LOGGER.info("delete with {}" , dto);
        checkArgument(dto.getStatus() == StatusType.PUBLISH || dto.getBasedVersion() == 0,
                "只有处于发布状态的版本文件或未发布过的文件才能被删除！");
        checkPublic(dto);
        try {
            applyService.delete(dto, "");
            return JsonV2.successOf(true);
        } catch (Exception e) {
            LOGGER.warn("occur error when deleting file!, {}", dto);
            return handleException(dto, e);
        }

    }

    private void checkPublic(CandidateDTO dto) {
        ConfigMeta meta = new ConfigMeta(dto.getGroup(), dto.getDataId(), dto.getProfile());
        boolean isPublic = filePublicStatusService.isPublic(new ConfigMetaWithoutProfile(dto.getGroup(), dto.getDataId()));
        if (!isPublic) return;
        checkArgument(eventPostReferenceService.beReferenceCount(meta) == 0, "该文件正在被别的应用引用，无法删除！");
    }

}
