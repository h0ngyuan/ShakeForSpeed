package com.sfs.websocket.service;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface BroadcastService {

    public void subscribe(String activityId, Channel channel);

    public void unsubscribeAll(Channel channel);

    public void broadcast(String activityId, Object message);
}