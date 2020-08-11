package com.hailin.admin.exception;


public class ValueCheckException extends RuntimeException {
    private static final long serialVersionUID = -5099293794124296965L;

    public ValueCheckException(String message) {
        super(message);
    }
}
