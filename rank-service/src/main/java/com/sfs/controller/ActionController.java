package com.sfs.controller;

import com.sfs.model.entity.UserShakeActionEvent;
import com.sfs.model.enums.GroupConstants;
import com.sfs.model.enums.TopicConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;

@Slf4j
@Controller
public class ActionController {


    @Autowired
    public KafkaTemplate<String,Object> kafkaTemplate;

    @MessageMapping("/shake")
    public void handleShake(@Payload UserShakeActionEvent userShakeActionEvent, StompHeaderAccessor accessor){
        String userId = accessor.getFirstNativeHeader("X-User-Id");
        String activityId = userShakeActionEvent.getActivityId();
        userShakeActionEvent.setUserId(Long.valueOf(userId));
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TopicConstants.SHAKE_FOR_SPEED_RAW, activityId, userShakeActionEvent);
        future.addCallback(
                success -> log.info("Kafka 发送成功: {}", activityId),
                failure -> log.error("Kafka 发送失败", failure)
        );
    }


}
