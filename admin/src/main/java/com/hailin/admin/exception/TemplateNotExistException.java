package com.hailin.admin.exception;


public class TemplateNotExistException extends RuntimeException{
    public TemplateNotExistException() {
        super("template Not exist");
    }

    public TemplateNotExistException(String message) {
        super(message);
    }

    public TemplateNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateNotExistException(Throwable cause) {
        super("template Not exist", cause);
    }

    public TemplateNotExistException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
