package com.hailin.server.common.util;

import java.util.regex.Pattern;

public class ProfileUtil {

    public static boolean affectProd(String profile) {
        Environment environment = Environment.fromProfile(profile);
        return environment.isResources() || environment.isProd();
    }

    public static boolean legalProfile(String profile) {
        try {
            Environment.fromProfile(profile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getEnvironment(String profile) {
        return profile.substring(0, profile.indexOf(':'));
    }

    public static String getBuildGroup(String profile) {
        return profile.substring(profile.indexOf(':') + 1);
    }

    public static Pattern BUILD_GROUP_LETTER_DIGIT_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]*$");
}