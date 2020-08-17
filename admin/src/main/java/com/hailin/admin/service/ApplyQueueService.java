package com.hailin.admin.service;

import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.model.ApplyResult;
import com.hailin.server.common.bean.CandidateSnapshot;

public interface ApplyQueueService {

    /**
     * 申请
     */
    ApplyResult apply(CandidateDTO dto);

    /**
     * 删除文件
     */
    void delete(CandidateDTO dto);

    /**
     * 强制提交，提交时不检查文件是否存在或已被删除，因为这个方法仅在创建一个与已删除的文件同名的文件时调用，
     * 直接把已删除的文件状态改为“待审核”，并更新其版本号和snapshot，流程与更新一个已发布的版本一致
     */
    void forceApply(CandidateDTO dto);

    void reject(CandidateDTO candidate);

    /**
     * 发布
     */
    CandidateSnapshot publish(CandidateDTO candidate);
}
