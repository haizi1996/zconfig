package com.hailin.admin.service.impl;

import com.google.common.base.Objects;
import com.hailin.admin.model.EncryptKey;
import com.hailin.admin.model.EncryptKeyStatus;
import com.hailin.admin.service.ConfigService;
import com.hailin.admin.service.EncryptKeyBlacklistService;
import com.hailin.admin.service.EncryptKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EncryptKeyServiceImpl implements EncryptKeyService {

    private static final Logger logger = LoggerFactory.getLogger(EncryptKeyServiceImpl.class);


    @Resource
    private ConfigService configService;

    @Resource
    private EncryptKeyBlacklistService encryptKeyBlacklistService;

    /**
     * 判断一个key是不是需要加密，判断加密的原则如下：
     * 1. 如果数据库中有key相关的加密配置数据，则以数据库为准
     * 2. 如果数据库中不存在key对应的配置数据，则以是否在黑名单中为准
     *
     * @param encryptKeys 数据库中保存的key和加密状态的设置列表
     * @param key         需要判断的key
     * @return 是否需要加密
     */
    @Override
    public boolean isEncryptedKey(List<EncryptKey> encryptKeys, String key) {
        for (EncryptKey encryptKey : encryptKeys) {
            // 如果数据库中存在设置，则以数据库中的设置数据为准
            if (Objects.equal(encryptKey.getKey(), key)) {
                return encryptKey.getStatus() == EncryptKeyStatus.ENCRYPTED;
            }
        }

        // 数据库中无设置数据，则以配置的黑名单为准
        return encryptKeyBlacklistService.inBlacklist(key);
    }
}
