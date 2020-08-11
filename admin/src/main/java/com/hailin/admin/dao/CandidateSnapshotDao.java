package com.hailin.admin.dao;

import com.hailin.server.common.bean.CandidateSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CandidateSnapshotDao {
    /**
     * 查询文件快照
     */
    CandidateSnapshot find(@Param("group") String group, @Param("dataId")String dataId, @Param("profile")String profile, @Param("editVersion")long editVersion);

    /**
     * 持久化文件快照
     */
    void save(CandidateSnapshot snapshot);
}
