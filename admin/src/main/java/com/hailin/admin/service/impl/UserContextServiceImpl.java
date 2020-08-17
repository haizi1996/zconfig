package com.hailin.admin.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.hailin.admin.service.ApplicationInfoService;
import com.hailin.admin.service.UserContextService;
import com.hailin.admin.web.security.Account;
import com.hailin.server.common.support.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class UserContextServiceImpl implements UserContextService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserContextServiceImpl.class);

    private static ThreadLocal<Account> account = new ThreadLocal<>();

    private static ThreadLocal<String> ip = new ThreadLocal<>();


    @Resource
    private ApplicationInfoService applicationInfoService;

    // group -> group info
    private ThreadLocal<Map<String, Application>> groups = new ThreadLocal<>();

    // 非本人所属的group
    private ThreadLocal<Map<String, Optional<Application>>> extraGroups = new ThreadLocal<>();

    private static final Application NOTEXIST_APPLICATION = new Application();

    static {
        NOTEXIST_APPLICATION.setCode("");
        NOTEXIST_APPLICATION.setCreateTime(new Date());
        NOTEXIST_APPLICATION.setCreator("");
        NOTEXIST_APPLICATION.setDeveloper(null);
        NOTEXIST_APPLICATION.setGroupCode("");
        NOTEXIST_APPLICATION.setMailGroup(null);
        NOTEXIST_APPLICATION.setName("");
        NOTEXIST_APPLICATION.setOwner(null);
        NOTEXIST_APPLICATION.setStatus(Application.Status.pass);
    }

    @Override
    public String getRtxId() {
        Account account = this.account.get();
        return account != null ? account.getUserId() : "";
    }

    @Override
    public String getIp() {
        return ip.get();
    }

    @Override
    public void setIp(String ip) {
        this.ip.set(ip);
    }

    @Override
    public Application getApplication(String group) {
        if (groups.get() == null || groups.get().get(group) == null) {
            Application application = getExtraGroupInfo(group);
            return application != null ? application : NOTEXIST_APPLICATION;
        } else {
            return groups.get().get(group);
        }
    }

    private Application getExtraGroupInfo(String group) {
        if (Strings.isNullOrEmpty(group)) {
            return null;
        }
        Map<String, Optional<Application>> extras = extraGroups.get();
        if (extras == null) {
            extras = Maps.newHashMap();
            extraGroups.set(extras);
        }

        Optional<Application> application = extras.get(group);
        if (application == null) {
            application = Optional.ofNullable(applicationInfoService.getGroupInfo(group));
            extras.put(group, application);
        }

        return application.orElse(null);
    }
}
