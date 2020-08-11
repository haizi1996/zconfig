package com.hailin.admin.exception;


import com.hailin.admin.model.Conflict;

public class ConfigExistException extends RuntimeException {

    private static final long serialVersionUID = -3650737665750308373L;

    private Conflict conflict;

    public ConfigExistException(Conflict conflict) {
        this.conflict = conflict;
    }

    public Conflict getConflict() {
        return conflict;
    }
}
