package com.hailin.zconfig.client;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;

/**
 * 配置文件接口
 * @param <T>
 */
public interface Configuration<T> {

    ListenableFuture<Boolean> initFuture();

    T emptyData();

    T parse(String data) throws IOException;

    void addListener(ConfigListener<T> listener);

    interface ConfigListener<T> {
        void onLoad(T conf);
    }

}
