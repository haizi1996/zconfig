package com.hailin.zconfig.client.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.hailin.zconfig.client.Feature;

import java.io.IOException;
import java.util.Map;

public class MapConfig extends AbstractConfiguration<Map<String, String>> {

    public MapConfig(Feature feature) {
        super(feature);
    }

    public MapConfig(Feature feature, String fileName) {
        super(feature, fileName);
    }

    @Override
    public ListenableFuture<Boolean> initFuture() {
        return null;
    }

    @Override
    public Map<String, String> emptyData() {
        return null;
    }

    @Override
    public Map<String, String> parse(String data) throws IOException {
        return null;
    }

    @Override
    public void addListener(ConfigListener<Map<String, String>> listener) {

    }

    public static MapConfig get(String groupName, String fileName, Feature feature) {
//        return (MapConfig) loader.load(groupName, fileName, feature, gen);
        return null;
    }

    public static MapConfig get(String fileName, Feature feature) {
        return get(null, fileName, feature);
    }

    public static MapConfig get(String fileName) {
        return get(null, fileName, Feature.DEFAULT);
    }

}
