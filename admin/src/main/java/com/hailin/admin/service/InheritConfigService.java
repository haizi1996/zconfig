package com.hailin.admin.service;

import com.hailin.admin.dto.CandidateDTO;

public interface InheritConfigService {

    void save(CandidateDTO dto, String rtxId);
}
