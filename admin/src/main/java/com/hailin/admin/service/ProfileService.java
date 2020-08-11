package com.hailin.admin.service;

import com.hailin.admin.exception.ModifiedException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProfileService {

    void create(String group, String profile) throws ModifiedException;

    void batchCreate(String group, Set<String> profile);

    List<String> find(String group);

    List<Map.Entry<String, String>> find(Collection<String> group);

    boolean exist(String group, String profile);

    void delete(String group, String profile);
}
