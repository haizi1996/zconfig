package com.hailin.admin.service;

import com.hailin.admin.model.EncryptKey;

import java.util.List;

public interface EncryptKeyService {

    boolean isEncryptedKey(List<EncryptKey> encryptKeys, String key);

}
