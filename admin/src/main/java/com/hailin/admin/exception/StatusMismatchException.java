package com.hailin.admin.exception;


public class StatusMismatchException extends RuntimeException {
    private static final long serialVersionUID = 3303191574555943554L;

    public StatusMismatchException() {
    }

    public StatusMismatchException(String message) {
        super(message);
    }
}
