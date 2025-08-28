package com.sfs.websocket.engine;

public interface ShakeCountEngine {

    void start();

    void stop();

    Long getTotalShakes(Long activityId);
}
