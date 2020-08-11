package com.hailin.admin.model;


import com.hailin.server.common.bean.Candidate;

public class Conflict {

    public enum Type { REF, EXIST, INHERIT}

    private Candidate candidate;

    private Type type;

    public Conflict(Candidate candidate, Type type) {
        this.candidate = candidate;
        this.type = type;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public Type getType() {
        return type;
    }
}
