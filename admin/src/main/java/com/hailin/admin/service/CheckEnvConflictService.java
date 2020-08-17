package com.hailin.admin.service;

import com.hailin.admin.model.Conflict;
import com.hailin.server.common.bean.ConfigMeta;

import java.util.Optional;

public interface CheckEnvConflictService {


    Optional<Conflict> getConflict(ConfigMeta meta);
}
