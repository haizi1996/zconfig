package com.hailin.admin.service.impl;

import com.hailin.admin.service.UserContextService;
import com.hailin.admin.web.security.Account;
import com.hailin.server.common.support.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserContextServiceImpl implements UserContextService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserContextServiceImpl.class);

    private static ThreadLocal<Account> account = new ThreadLocal<>();

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
}
