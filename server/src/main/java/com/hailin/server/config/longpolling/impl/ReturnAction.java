package com.hailin.server.config.longpolling.impl;

import javax.servlet.AsyncContext;

public interface ReturnAction {

    String type();

    void act(AsyncContext context) throws Exception;
}
