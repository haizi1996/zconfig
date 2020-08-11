package com.hailin.admin.service;

import com.hailin.admin.support.Differ;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;

import java.util.List;
import java.util.Map;

public interface DiffService {

    Map.Entry<VersionData<ConfigMeta>, Differ.MixedDiffResult<String,String>> getHtmlMixedDiffToLastPublish(ConfigMeta meta, String data);

    List<Map.Entry<VersionData<ConfigMeta>, Differ.MixedDiffResult<String, String>>> getHtmlMixedDiffToRelativeProfile(ConfigMeta meta, String data);
}
