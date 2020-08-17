package com.hailin.admin.dao;

import com.hailin.server.common.bean.Candidate;
import com.hailin.zconfig.common.bean.StatusType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CandidateDao {

    int update(@Param("candidate")Candidate candidate, @Param("sourceStatus") StatusType statusType );

    void save(Candidate candidate);
}
