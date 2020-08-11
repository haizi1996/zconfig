package com.hailin.admin.exception;


public class TemplateNameNotMatchException extends RuntimeException {

    public TemplateNameNotMatchException() {
        super("file limit name not match template limit");
    }
}
