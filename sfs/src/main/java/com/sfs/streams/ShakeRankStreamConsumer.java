package com.sfs.streams;

import com.sfs.service.BroadcastService;
import com.sfs.service.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShakeRankStreamConsumer {

    private final RankService rankService;
    private final BroadcastService broadcastService;

    /**
     * 消费 Kafka Streams 聚合后的排行数据
     * Topic: shake-ranks
     * 格式: activityId,userId,count,timestamp
     */
    @KafkaListener(topics = "${app.kafka.topics.shake-ranks:shake-ranks}", groupId = "sfs-rank-group")
    public void onShakeRank(String message) {
        try {
            log.debug("Processing shake rank event: {}", message);
            String[] parts = message.split(",");
            if (parts.length >= 4) {
                String activityId = parts[0];
                String userId = parts[1];
                int count = Integer.parseInt(parts[2]);
                long timestamp = Long.parseLong(parts[3]);

                // 1. 更新 Redis 排行 (使用 Streams 聚合后的窗口计数)
                rankService.addScore(Long.parseLong(activityId), userId, count);

                // 2. 广播排行更新
                broadcastService.broadcastRank(activityId, getRankJson(activityId));

                log.trace("Rank updated: activityId={}, userId={}, count={}", activityId, userId, count);
            }
        } catch (Exception e) {
            log.error("Failed to process shake rank event: {}", message, e);
        }
    }

    private String getRankJson(String activityId) {
        var ranks = rankService.getRealtimeRank(Long.parseLong(activityId), 10);
        return "{\"type\":\"rank_update\",\"rank\":" + ranks + "}";
    }
}
