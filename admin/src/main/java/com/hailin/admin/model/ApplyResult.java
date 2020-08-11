package com.hailin.admin.model;

public enum ApplyResult {

    NEW, UPDATE;

    public static ApplyResult of(long editVersion) {
        return (editVersion == 0 ? NEW : UPDATE);
    }

}
