package com.hailin.server.web;

import com.hailin.server.common.bean.ChecksumData;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import com.hailin.server.config.zfile.ZFile;
import com.hailin.server.exception.AccessForbiddenException;
import com.hailin.server.exception.ConfigNotFoundException;
import com.hailin.server.service.ConfigService;
import com.hailin.zconfig.common.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/client")
public class ClientConfigController {

    protected static final Logger logger = LoggerFactory.getLogger(ClientConfigController.class);

    @Resource
    private ConfigService configService;

    @GetMapping("/getConfig")
    public Objects getConfig( String group ,  String dataId , Long version , @RequestParam("loadProfile") String profile ,HttpServletResponse resp ) throws IOException {
        ConfigMeta queryMeta = new ConfigMeta(group, dataId, profile);
        VersionData<ConfigMeta> versionData = VersionData.of(version, queryMeta);
        if (logger.isDebugEnabled()) {
            logger.debug("get config, {}", queryMeta);
        }
        try {
            Map.Entry<ZFile, VersionData<ChecksumData<String>>> fileConfig = configService.loadConfig(versionData);

        } catch (ConfigNotFoundException e) {
            logger.warn("未能定位到配置文件, {}. {}", versionData, e);
            resp.setHeader(Constants.VERSION_NAME, String.valueOf(version));
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (AccessForbiddenException e) {
            logger.warn("forbid unpublic diff group access, client group [{}], group [{}], dataId [{}]", clientInfoService.getGroup(), group, dataId);
            resp.addHeader(Constants.FORBIDDEN_FILE, group + ":" + dataId);
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } catch (Throwable e) {
            logger.error("服务器内部异常, {}. {}" + versionData, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }finally {
            resp.flushBuffer();
        }
    }

}
