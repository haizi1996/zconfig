package com.hailin.admin.service.impl;

import com.hailin.admin.exception.ModifiedException;
import com.hailin.admin.service.ProfileService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Override
    public void create(String group, String profile) throws ModifiedException {

    }

    @Override
    public void batchCreate(String group, Set<String> profile) {

    }

    @Override
    public List<String> find(String group) {
        return null;
    }

    @Override
    public List<Map.Entry<String, String>> find(Collection<String> group) {
        return null;
    }

    @Override
    public boolean exist(String group, String profile) {
        return false;
    }

    @Override
    public void delete(String group, String profile) {

    }
}
