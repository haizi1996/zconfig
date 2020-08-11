package com.hailin.admin.web.security;

import lombok.Data;

@Data
public class Account {

    private String userId;

    private String type;

    public Account() {
    }

    public Account(String userId) {
        this(userId, "user");
    }

    public Account(String userId, String type) {
        this.userId = userId;
        this.type = type;
    }
}
