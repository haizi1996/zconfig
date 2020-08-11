package com.hailin.admin.service.impl;

import com.hailin.admin.model.DiffResult;
import com.hailin.admin.service.ConfigService;
import com.hailin.admin.service.DiffService;
import com.hailin.admin.support.Differ;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class DiffServiceImpl implements DiffService {

    @Resource
    private ConfigService configService;

    @Resource
    private Differ differ;

    @Override
    public Map.Entry<VersionData<ConfigMeta>, Differ.MixedDiffResult<String, String>> getHtmlMixedDiffToLastPublish(ConfigMeta meta, String data) {
        VersionData<String> oldVersionData = configService.getCurrentPublishedData(meta);
        DiffResult<String> htmlDiff = differ.diffToHtml(oldVersionData.getData(), data, meta.getDataId());
        DiffResult<String> uniDiff = differ.uniDiff(oldVersionData.getData(), data, meta.getDataId());
        return null;
    }

    @Override
    public List<Map.Entry<VersionData<ConfigMeta>, Differ.MixedDiffResult<String, String>>> getHtmlMixedDiffToRelativeProfile(ConfigMeta meta, String data) {
        return null;
    }
}
