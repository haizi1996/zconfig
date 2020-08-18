package com.hailin.server.common.util;

import com.google.common.collect.Lists;
import com.hailin.server.common.bean.ConfigMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PriorityUtil {

    private static Logger logger = LoggerFactory.getLogger(PriorityUtil.class);

    public static List<ConfigMeta> createPriorityListWithRoom(ConfigMeta meta, String room) {
        List<ConfigMeta> priorityList = Lists.newArrayList();
        Environment env;
        try {
            env = Environment.fromProfile(meta.getProfile());
            // add self
            priorityList.add(meta);
        } catch (IllegalArgumentException e) {
            logger.info("create subenv candidate from profile exception: {}", e);
            env = Environment.extractDefaultProfile(meta.getProfile());
        }
        return null;
    }
}
