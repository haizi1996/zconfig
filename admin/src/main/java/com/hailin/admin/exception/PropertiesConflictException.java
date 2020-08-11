package com.hailin.admin.exception;


public class PropertiesConflictException extends RuntimeException {

    private String key;

    public PropertiesConflictException(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
