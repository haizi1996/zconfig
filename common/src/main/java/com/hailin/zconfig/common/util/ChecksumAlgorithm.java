package com.hailin.zconfig.common.util;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;

public final class ChecksumAlgorithm {

    public static String getChecksum(String data) {
        if (Strings.isNullOrEmpty(data)) return Constants.NO_FILE_CHECKSUM;
        return Hashing.murmur3_128().hashString(data, Charsets.UTF_8).toString();
    }
}
