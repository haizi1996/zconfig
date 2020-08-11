package com.hailin.admin.exception;


public class IllegalTemplateException extends RuntimeException {
    private static final long serialVersionUID = 2340595586974470999L;

    private String template;

    public IllegalTemplateException(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
