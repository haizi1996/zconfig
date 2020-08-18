package com.hailin.admin.cloud.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hailin.admin.cloud.vo.ConfigMetaVersion;
import com.hailin.admin.cloud.vo.DiffInfosVo;
import com.hailin.admin.dao.ConfigOpLogDao;
import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.model.DiffCount;
import com.hailin.admin.model.DiffResultView;
import com.hailin.server.common.bean.KeyValuePair;
import com.hailin.admin.service.DiffService;
import com.hailin.admin.service.FileTemplateService;
import com.hailin.admin.service.template.TemplateUtils;
import com.hailin.admin.support.DiffUtil;
import com.hailin.admin.support.Differ;
import com.hailin.admin.support.JsonUtil;
import com.hailin.admin.web.controller.AbstractControllerHelper;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import com.hailin.server.common.util.Environment;
import com.hailin.zconfig.common.bean.JsonV2;
import com.hailin.zconfig.common.util.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/zconfig/diff")
public class ContentDiffController extends AbstractControllerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentDiffController.class);

    @Resource
    private ConfigOpLogDao configOpLogDao;

    @Value("${profileConfigLog.showLength}")
    private int profileConfigLogLength;

    @Value("${profileRefLog.showLength}")
    private int profileRefLogLength;

    @Value("${configLog.showLength}")
    private int configLogLength;

    private FileTemplateService fileTemplateService;

    @Resource
    private Differ differ;

    @Resource
    private DiffService diffService;

    @PostMapping("/applyInfo")
    public Object applyInfo(@RequestBody CandidateDTO dto) {
        ConfigMeta meta = new ConfigMeta(dto.getGroup(), dto.getDataId(), dto.getProfile());
        checkLegalMeta(meta);
        String data = dto.getData();
        if (FileChecker.isTemplateFile(dto.getDataId())) {

        }
        long baseVersion = dto.getEditVersion();
        try {
            return JsonV2.successOf(wrapDiffAlert(getCompareInfo(meta, data, baseVersion)));
        }catch (Exception e){
            LOGGER.error("get apply compare info error, {}", dto, e);
            return JsonV2.failOf("系统发生异常，请与管理员联系！");
        }
    }
    // html格式的diff
    private DiffInfosVo getCompareInfo(ConfigMeta meta, String data, long baseVersion) {
        boolean isTemplateFile = FileChecker.isTemplateFile(meta.getDataId());
        if (FileChecker.isJsonFile(meta.getDataId())) {
            Optional<JsonNode> dataJson = JsonUtil.read(data);
            if (!dataJson.isPresent()) {
                return new DiffInfosVo(meta, data, null, null, Lists.<Map.Entry<VersionData<ConfigMeta>, JsonNode>>newArrayList(), configOpLogDao.selectRecent(meta,
                        baseVersion, configLogLength));
            }
        }else {
            String templateDetail = "";
            if (isTemplateFile) {
                Optional<String> detail = fileTemplateService.getTemplateDetailByFile(meta.getGroup(), meta.getDataId());
                if (detail.isPresent()) {
                    templateDetail = detail.get();
                    Optional<String> optional = TemplateUtils.processTimeLongToStr(meta.getDataId(), data, templateDetail);
                    if (optional.isPresent()) {
                        data = optional.get();
                    }
                }
            }
            Map.Entry<VersionData<ConfigMeta>, Differ.MixedDiffResult<String, String>> lastPublishDiff = diffService.getHtmlMixedDiffToLastPublish(meta, data);
            List<Map.Entry<VersionData<ConfigMeta>, Differ.MixedDiffResult<String, String>>> relativeDiffs = diffService.getHtmlMixedDiffToRelativeProfile(meta, data);
            List<Map.Entry<VersionData<ConfigMeta>, DiffResultView>> diffs = Lists.newArrayListWithCapacity(relativeDiffs.size() + 1);
            diffs.add(diffVo(lastPublishDiff));
            for (Map.Entry<VersionData<ConfigMeta>, Differ.MixedDiffResult<String, String>> diff : relativeDiffs) {
                diffs.add(diffVo(diff));
            }
            return new DiffInfosVo(meta, data, null, templateDetail, diffs,
                    configOpLogDao.selectRecent(meta, baseVersion, configLogLength));
        }
        return null;
    }

    private DiffInfosVo wrapDiffAlert(DiffInfosVo diffInfosVo) {
        List<KeyValuePair<ConfigMetaVersion, Object>> diffs = diffInfosVo.getDiffs();
        if (diffs.size() <= 1) {
            return diffInfosVo;
        }
        Environment env = Environment.fromProfile(diffInfosVo.getProfile());
        if (!env.isProd() && !env.isResources()) {
            // show alert in prod/resources only
            return diffInfosVo;
        }
        KeyValuePair<ConfigMetaVersion, Object> relativeDiff = diffs.get(1);
        Environment relativeEnv = Environment.fromProfile(relativeDiff.getKey().getProfile());
        if (FileChecker.isJsonFile(diffInfosVo.getDataId())) {
            JsonNode jsonNode = (JsonNode)relativeDiff.getValue();
            if (jsonNode == null) {
                diffInfosVo.setShowDiffAlert(true);
                diffInfosVo.setDiffAlertText(String.format("该文件在%s环境中尚未发布过, 请仔细确认!", relativeEnv.env()));
            }

        } else {
            DiffResultView diffResultView = (DiffResultView)relativeDiff.getValue();
            if (diffResultView == null) {
                diffInfosVo.setShowDiffAlert(true);
                diffInfosVo.setDiffAlertText(String.format("该文件在%s环境中尚未发布过, 请仔细确认!", relativeEnv.env()));
                return diffInfosVo;
            }
            // properties检查key set是否一致
            if (FileChecker.isPropertiesFile(diffInfosVo.getDataId())) {
                DiffCount diffCount = diffResultView.getDiffCount();
                if (diffCount.getAdd() != 0 || diffCount.getDelete() != 0) {
                    diffInfosVo.setShowDiffAlert(true);
                    StringBuilder stringBuilder = new StringBuilder("该文件对比").append(relativeEnv.env()).append("环境");
                    if (diffCount.getAdd() != 0) {
                        stringBuilder.append("新增了").append(diffCount.getAdd()).append("个key, ");
                    }
                    if (diffCount.getDelete() != 0) {
                        stringBuilder.append("删除了").append(diffCount.getDelete()).append("个key, ");
                    }
                    stringBuilder.append("请仔细确认!");
                    diffInfosVo.setDiffAlertText(stringBuilder.toString());
                }
            }
        }
        return diffInfosVo;
    }

    //origin
    private Map.Entry<VersionData<ConfigMeta>, DiffResultView> diffVo(Map.Entry<VersionData<ConfigMeta>, Differ.MixedDiffResult<String, String>> diff) {
        Differ.MixedDiffResult<String, String> value = diff.getValue();
        VersionData<ConfigMeta> versionMeta = diff.getKey();
        String name = versionMeta.getData().getDataId();
        return Maps.immutableEntry(versionMeta, DiffUtil.wrapDiffVo(name, value));
    }
}
