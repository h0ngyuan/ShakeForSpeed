package com.sfs.websocket.service;

import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.broadcast-mode", havingValue = "webflux")
public class WebFluxBroadcastService implements BroadcastService{

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void broadcast(String activityId, Object message) {
        String destination = "/topic/activity/" + activityId;
        messagingTemplate.convertAndSend(destination, message);
    }

    @Override
    public void subscribe(String activityId, Channel channel) {}

    @Override
    public void unsubscribeAll(Channel channel) {}

}
