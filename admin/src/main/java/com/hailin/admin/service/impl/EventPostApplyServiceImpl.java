package com.hailin.admin.service.impl;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.event.CandidateDTONotifyBean;
import com.hailin.admin.event.ConfigOperationEvent;
import com.hailin.admin.exception.ConfigExistException;
import com.hailin.admin.exception.IllegalTemplateException;
import com.hailin.admin.exception.ModifiedException;
import com.hailin.admin.exception.StatusMismatchException;
import com.hailin.admin.exception.TemplateChangedException;
import com.hailin.admin.model.ApplyResult;
import com.hailin.admin.service.ApplyQueueService;
import com.hailin.admin.service.EventPostApplyService;
import com.hailin.admin.service.UserContextService;
import com.hailin.server.common.bean.CandidateSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class EventPostApplyServiceImpl implements EventPostApplyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPostApplyServiceImpl.class);

    @Resource
    private ApplyQueueService applyService;

    @Resource
    private UserContextService userContextService;

    @Resource
    private EventBus eventBus;

    @Resource
    private AsyncEventBus asyncEventBus;

    @Override
    public void forceApply(CandidateDTO dto, String remarks) throws StatusMismatchException, ModifiedException, TemplateChangedException, IllegalTemplateException {
        applyService.forceApply(dto);
        eventBus.post(
                new CandidateDTONotifyBean(ConfigOperationEvent.NEW, dto ,  userContextService.getIp() , userContextService.getApplication(dto.getGroup()) ,userContextService.getRtxId(),
                        remarks));
    }

    @Override
    public void apply(CandidateDTO dto, String remarks) throws ModifiedException, StatusMismatchException, ConfigExistException, TemplateChangedException, IllegalTemplateException {
        ApplyResult result =applyService.apply(dto);
        postApplyEvent(dto, remarks, result);
    }

    @Override
    public void delete(CandidateDTO dto, String s) {
        applyService.delete(dto);

    }

    private void postApplyEvent(CandidateDTO dto, String remarks, ApplyResult result) {
        if (result == ApplyResult.NEW) {
            eventBus.post(
                    new CandidateDTONotifyBean(ConfigOperationEvent.NEW, dto,  userContextService.getIp()
                             , userContextService.getApplication(dto.getGroup()) , userContextService.getRtxId(), remarks));
        } else if (result == ApplyResult.UPDATE) {
            eventBus.post(
                    new CandidateDTONotifyBean(ConfigOperationEvent.UPDATE, dto,  userContextService.getIp()
                             , userContextService.getApplication(dto.getGroup()) , userContextService.getRtxId(),remarks ));
        }
    }

    @Override
    public void reject(CandidateDTO candidate, String remarks) {
        applyService.reject(candidate);
        eventBus.post(
                new CandidateDTONotifyBean(ConfigOperationEvent.REJECT, candidate , userContextService.getIp() , userContextService.getApplication(candidate.getGroup()) ,userContextService.getRtxId(),
                         remarks));
    }

    @Override
    public void publish(CandidateDTO candidate, String remarks) {
        CandidateSnapshot snapshot = applyService.publish(candidate);
        candidate.setData(snapshot.getData());
        candidate.setSendMail(true);
//        loadDataAndPostEvent(candidate, ConfigOperationEvent.PUBLISH, remarks);
//        postCurrentConfigChangedEvent(candidate, ConfigOperationEvent.PUBLISH);
//        loadInheritdataAndPostInheritEvent(candidate);
    }
}
