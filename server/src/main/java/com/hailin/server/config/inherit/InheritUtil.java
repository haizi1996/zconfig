package com.hailin.server.config.inherit;

import com.google.common.base.Optional;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.util.PriorityUtil;

import java.util.List;

public class InheritUtil {
    public static Optional<InheritMeta> getInheritRelationWithFuzzyRelation(InheritMeta fuzzyMeta, InheritJudgement inheritJudgement , String room){
        List<ConfigMeta> orderedCandidateParents = PriorityUtil.createPriorityListWithRoom(fuzzyMeta.getParent(), room);
        List<ConfigMeta> orderedCandidateChildren = PriorityUtil.createPriorityListWithRoom(fuzzyMeta.getChild(), room);

    }
}
