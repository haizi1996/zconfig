package com.hailin.server.config.longpolling.impl;

import com.hailin.server.common.bean.IpAndPort;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

@Getter
@ToString
public class AsyncContextHolder {

    private static final Logger logger = LoggerFactory.getLogger(AsyncContextHolder.class);

    private volatile boolean complete = false;

    private final AsyncContext context;

    private final String ip;

    private final int port;

    public AsyncContextHolder(AsyncContext context, IpAndPort ipAndPort) {
        this.context = context;
        this.ip = ipAndPort.getIp();
        this.port = ipAndPort.getPort();
    }

    public void completeRequest(ReturnAction returnAction) {
        if (complete) {
            return;
        }

        synchronized (this) {
            if (complete) {
                return;
            }

            complete = true;
            try {
                logger.info("do return {} to [{}:{}]", returnAction.type(), ip, port);
                returnAction.act(context);
            } catch (Exception e) {
                logger.info("do return {} to [{}:{}] error", returnAction.type(), ip, port, e);
                ((HttpServletResponse) context.getResponse()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } finally {
                context.complete();
            }
        }
    }

}
