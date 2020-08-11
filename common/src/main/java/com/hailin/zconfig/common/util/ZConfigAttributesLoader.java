package com.hailin.zconfig.common.util;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZConfigAttributesLoader {

    private static final Logger logger = LoggerFactory.getLogger(ZConfigAttributesLoader.class);

    private static final DefaultPropertiesLoader defaultPropertiesLoader = DefaultPropertiesLoader.getInstance();

    private static final String DEFAULT_ADDRESS_KEY = "zconfig.default.serverlist";
    private static final String DEFAULT_HTTPS_ADDRESSES_KEY = "zconfig.default.httpsserverlist";
    private static final String SERVER_URL_key = "zconfig.server.host";
    private static final String ADMIN_URL_KEY = "zconfig.admin.host";
    private static final String DEV_KEY = "zconfig.symbol.dev";
    private static final String BETA_KEY = "zconfig.symbol.beta";
    private static final String PROD_KEY = "zconfig.symbol.prod";
    private static final String RESOURCES_KEY = "zconfig.symbol.resources";
    private static final String BUILDGROUP_KEY ="zconfig.symbol.buildgroup";
    private static final String SERVERAPP_KEY = "zconfig.server.app";
    private static final String REGISTER_SELF_ON_START_KEY = "zconfig.server.register_self_on_start";

    private static final Supplier<ZConfigAttributes> attrSupplier = Suppliers.memoize(new Supplier<ZConfigAttributes>() {
        @Override
        public ZConfigAttributes get() {
            String resources = System.getProperty(RESOURCES_KEY, defaultPropertiesLoader.getDefaultValue(RESOURCES_KEY));
            String buildGroup = System.getProperty(BUILDGROUP_KEY, defaultPropertiesLoader.getDefaultValue(BUILDGROUP_KEY));
            String defaultAddresses = System.getProperty(DEFAULT_ADDRESS_KEY, defaultPropertiesLoader.getDefaultValue(DEFAULT_ADDRESS_KEY));
            String defaultHttpsAddresses = System.getProperty(DEFAULT_HTTPS_ADDRESSES_KEY, defaultPropertiesLoader.getDefaultValue(DEFAULT_HTTPS_ADDRESSES_KEY));
            String serverUrl = System.getProperty(SERVER_URL_key, defaultPropertiesLoader.getDefaultValue(SERVER_URL_key));
            String adminUrl = System.getProperty(ADMIN_URL_KEY, defaultPropertiesLoader.getDefaultValue(ADMIN_URL_KEY));
            String dev = System.getProperty(DEV_KEY, defaultPropertiesLoader.getDefaultValue(DEV_KEY));
            String beta = System.getProperty(BETA_KEY, defaultPropertiesLoader.getDefaultValue(BETA_KEY));
            String prod = System.getProperty(PROD_KEY, defaultPropertiesLoader.getDefaultValue(PROD_KEY));
            String serverApp = System.getProperty(SERVERAPP_KEY, defaultPropertiesLoader.getDefaultValue(SERVERAPP_KEY));
            boolean registerSelfOnStart = Boolean.valueOf(System.getProperty(REGISTER_SELF_ON_START_KEY, defaultPropertiesLoader.getDefaultValue(REGISTER_SELF_ON_START_KEY)));

            logger.info("zconfig attributes, default addressed [{}], server url [{}], admin url [{}], " +
                            "dev [{}], beta [{}], prod [{}], resources [{}], build group [{}], server app [{}], register self on start[{}]",
                    defaultAddresses, serverUrl, adminUrl, dev, beta, prod, resources, buildGroup, serverApp, registerSelfOnStart);

            return new ZConfigAttributes.Builder()
                    .setDefaultAddresses(defaultAddresses)
                    .setDefaultHttpsAddresses(defaultHttpsAddresses)
                    .setServerUrl(serverUrl)
                    .setAdminUrl(adminUrl)
                    .setDev(dev)
                    .setBeta(beta)
                    .setProd(prod)
                    .setResources(resources)
                    .setBuildGroup(buildGroup)
                    .setServerApp(serverApp)
                    .setRegisterSelfOnStart(registerSelfOnStart)
                    .build();
        }
    });

    public static ZConfigAttributes getInstance() {
        return attrSupplier.get();
    }
}
