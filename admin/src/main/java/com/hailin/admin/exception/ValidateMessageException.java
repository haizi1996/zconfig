package com.hailin.admin.exception;


public class ValidateMessageException extends RuntimeException {
    public ValidateMessageException() {
    }

    public ValidateMessageException(String message) {
        super(message);
    }

    public ValidateMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidateMessageException(Throwable cause) {
        super(cause);
    }
}
