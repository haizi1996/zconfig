package com.hailin.server.util;

import com.hailin.server.domain.Changed;

import java.util.Collection;

public class OutputUtil {

    public static String formatOutput(Collection<Changed> changedList) {
        StringBuilder builder = new StringBuilder();
        for (Changed changed : changedList) {
            builder.append(changed.getGroup()).append(",")
                    .append(changed.getDataId()).append(",")
                    .append(changed.getNewestVersion()).append(",")
                    .append(changed.getProfile()).append(Constants.LINE);
        }
        return builder.toString();
    }
}
