package com.hailin.admin.service;

import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.model.ApplyResult;

public interface ApplyQueueService {

    /**
     * 申请
     */
    ApplyResult apply(CandidateDTO dto);

    /**
     * 删除文件
     */
    void delete(CandidateDTO dto);
}
