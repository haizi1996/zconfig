package com.hailin.admin.service.impl;

import com.hailin.admin.dao.InheritConfigDao;
import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.model.Reference;
import com.hailin.admin.service.InheritConfigService;
import com.hailin.server.common.util.RefType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class InheritConfigServiceImpl implements InheritConfigService {

    @Resource
    private InheritConfigDao inheritConfigDao;

    @Override
    public void save(CandidateDTO dto, String operator) {
        Reference reference = new Reference();
        reference.setType(RefType.INHERIT.value());
        reference.setProfile(dto.getProfile());
        reference.setAlias(dto.getDataId());
        reference.setGroup(dto.getGroup());
        reference.setOperator(operator);
        reference.setRefDataId(dto.getInheritDataId());
        reference.setRefGroup(dto.getInheritGroupId());
        reference.setRefProfile(dto.getInheritProfile());
        inheritConfigDao.save(reference);
    }
}
