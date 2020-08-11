package com.hailin.admin.service.impl;

import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.exception.ConfigExistException;
import com.hailin.admin.exception.IllegalTemplateException;
import com.hailin.admin.exception.ModifiedException;
import com.hailin.admin.exception.StatusMismatchException;
import com.hailin.admin.exception.TemplateChangedException;
import com.hailin.admin.service.ApplyQueueService;
import com.hailin.admin.service.EventPostApplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EventPostApplyServiceImpl implements EventPostApplyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPostApplyServiceImpl.class);

    @Resource
    private ApplyQueueService applyService;

    @Override
    public void forceApply(CandidateDTO dto, String remarks) throws StatusMismatchException, ModifiedException, TemplateChangedException, IllegalTemplateException {

    }

    @Override
    public void apply(CandidateDTO dto, String remarks) throws ModifiedException, StatusMismatchException, ConfigExistException, TemplateChangedException, IllegalTemplateException {

    }

    @Override
    public void delete(CandidateDTO dto, String s) {
        applyService.delete(dto);

    }
}
