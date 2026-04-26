package com.sfs.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShakeEventConsumer {

    /**
     * 原始 shake-events 消费者（仅做日志和监控）
     * 实际的流式处理由 Kafka Streams 完成
     */
    @KafkaListener(topics = "shake-events", groupId = "sfs-shake-monitor-group")
    public void onShakeEvent(String message) {
        try {
            log.debug("Raw shake event received for monitoring: {}", message);
            String[] parts = message.split(",");
            if (parts.length >= 4) {
                String activityId = parts[0];
                String userId = parts[1];
                int score = Integer.parseInt(parts[2]);
                long timestamp = Long.parseLong(parts[3]);

                // 这里可以做监控、日志或反作弊检测
                log.trace("Shake event monitored: activityId={}, userId={}, score={}", activityId, userId, score);
            }
        } catch (Exception e) {
            log.error("Failed to monitor shake event: {}", message, e);
        }
    }
}
