package com.sfs.websocket.service;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketBroadcastService {

    private final Map<String, Set<Channel>> subscribers = new ConcurrentHashMap<>();

    public void subscribe(String activityId, Channel channel){
        subscribers.computeIfAbsent(activityId,k->ConcurrentHashMap.newKeySet()).add(channel);
    }

    public void unsubscribeAll(Channel channel){
        subscribers.values().forEach(set->set.remove(channel));
    }

    public void broadcast(String activityId,Object message){
        Set<Channel> channels = subscribers.get(activityId);
        if (channels!=null){
            String json = JSON.toJSONString(message);
            TextWebSocketFrame frame = new TextWebSocketFrame(json);
            channels.stream()
                    .filter(Channel::isActive)
                    .forEach(ch->ch.writeAndFlush(frame));
        }
    }
}
