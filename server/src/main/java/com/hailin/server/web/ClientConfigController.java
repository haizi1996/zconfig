package com.hailin.server.web;

import com.hailin.server.vo.GetConfigMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/client")
public class ClientConfigController {

    protected static final Logger logger = LoggerFactory.getLogger(ClientConfigController.class);

    @GetMapping("/getconfig")
    public Objects getConfig(  GetConfigMeta configMeta){
        if (logger.isDebugEnabled()) {
            logger.debug("get config, {}", configMeta);
        }

        Map.Entry<QFile, VersionData<ChecksumData<String>>> fileConfig = loadConfig(versionData);
    }

}
