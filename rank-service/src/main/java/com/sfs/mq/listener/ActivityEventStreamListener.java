package com.sfs.mq.listener;

import com.sfs.model.entity.UserShakeActionEvent;
import com.sfs.model.enums.GroupConstants;
import com.sfs.model.enums.TopicConstants;
import com.sfs.websocket.service.BroadcastService;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ActivityEventStreamListener {

    @Autowired
    private BroadcastService broadcastService;

    @KafkaListener(topics = TopicConstants.SHAKE_FOR_SPEED_PROCESSED, groupId = GroupConstants.PARTICIPATE_GROUP)
    public void shakeEvent(String activityId, String payload){
        broadcastService.broadcast(activityId,payload);
    }
}
