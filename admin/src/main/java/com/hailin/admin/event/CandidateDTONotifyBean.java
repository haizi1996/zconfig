package com.hailin.admin.event;

import com.hailin.admin.dto.CandidateDTO;
import com.hailin.server.common.support.Application;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class CandidateDTONotifyBean {

    public final ConfigOperationEvent event;
    public final CandidateDTO candidateDTO;
    public final String ip;
    public final Application application;
    public final String operator;
    public final String remarks;



    public CandidateDTONotifyBean copy() {
        return new CandidateDTONotifyBean(event,  candidateDTO.copy() ,  ip,  application, operator, remarks);
    }
}
