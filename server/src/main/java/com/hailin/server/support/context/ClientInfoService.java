package com.hailin.server.support.context;

public interface ClientInfoService {
    String getGroup();

    void setGroup(String group);

    String getCorp();

    void setCorp(String corp);

    String getEnv();

    void setEnv(String env);

    String getProfile();

    void setProfile(String profile);

    String getIp();

    void setIp(String ip);

    int getPort();

    void setPort(int port);

    boolean isNoToken();

    void setNoToken(Boolean noToken);

    String getRoom();

    void setRoom(String room);

    void clear();
}
