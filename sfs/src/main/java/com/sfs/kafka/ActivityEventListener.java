package com.sfs.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActivityEventListener {

    @KafkaListener(topics = "activity-events", groupId = "sfs-group")
    public void onActivityEvent(String message) {
        log.info("Received activity event: {}", message);
    }
}
