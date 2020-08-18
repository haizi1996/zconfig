package com.hailin.server.config.zfile;


import com.hailin.server.common.bean.ChecksumData;
import com.hailin.server.common.bean.ConfigMeta;
import com.hailin.server.common.bean.VersionData;
import com.hailin.server.config.longpolling.Listener;
import com.hailin.server.config.longpolling.impl.AsyncContextHolder;
import com.hailin.server.domain.Changed;
import com.hailin.server.domain.CheckRequest;
import com.hailin.server.exception.ConfigNotFoundException;

import java.util.Optional;

public interface ZFile {

    // 代表当前文件
    ConfigMeta getSourceMeta();

    // 如果当前文件是共享了上一级文件的情况，那么返回上一级文件，否则返回自身
    ConfigMeta getSharedMeta();

    // 返回最终的文件，也就是文件内容真正所在的那一个
    ConfigMeta getRealMeta();

    Optional<Changed> checkChange(CheckRequest request, String ip);

    ChecksumData<String> findConfig(long version) throws ConfigNotFoundException;

    VersionData<ChecksumData<String>> forceLoad(String ip, long version) throws ConfigNotFoundException;

    Listener createListener(CheckRequest request, AsyncContextHolder contextHolder);

}
