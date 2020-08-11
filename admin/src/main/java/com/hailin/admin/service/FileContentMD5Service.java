package com.hailin.admin.service;

import com.hailin.server.common.bean.CandidateSnapshot;

public interface FileContentMD5Service {

    void applyConfigChange(CandidateSnapshot snapshot);

}
