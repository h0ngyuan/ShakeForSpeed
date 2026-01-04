package com.sfs.controller;

import com.sfs.model.entity.UserAccount;
import com.sfs.model.entity.UserShakeActionEvent;
import com.sfs.model.enums.TopicConstants;
import com.sfs.utils.context.AccountContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Controller
public class ActionController {


    @Autowired
    public KafkaTemplate<String,Object> kafkaTemplate;

    @MessageMapping("/shake")
    public void handleShake(@Payload UserShakeActionEvent userShakeActionEvent, StompHeaderAccessor accessor){
        // 从线程上下文获取用户信息
        UserAccount userAccount = AccountContext.getAccount();
        if (userAccount == null) {
            // 尝试从会话属性中获取用户信息
            userAccount = (UserAccount) accessor.getSessionAttributes().get("userAccount");
        }
        
        if (userAccount == null) {
            log.error("WebSocket消息处理失败：无法获取用户信息");
            return;
        }
        
        Long userId = Long.valueOf(userAccount.getUid());
        String activityId = userShakeActionEvent.getActivityId();
        userShakeActionEvent.setUserId(userId);
        
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TopicConstants.SHAKE_FOR_SPEED_RAW, activityId, userShakeActionEvent);
        future.addCallback(
                success -> log.info("Kafka 发送成功: {}", activityId),
                failure -> log.error("Kafka 发送失败", failure)
        );
    }


}
