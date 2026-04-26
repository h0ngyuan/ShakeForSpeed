package com.sfs.service;

import com.sfs.flink.FlinkJobManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShakeEventService {

    private final FlinkJobManager flinkJobManager;
    private final BroadcastService broadcastService;

    /**
     * 活动开始时调用，触发流式处理
     */
    public void onActivityStart(Long activityId) {
        log.info("Activity {} started, Kafka Streams will process shake events", activityId);
        // Kafka Streams 会自动处理 shake-events topic 的消息
    }

    /**
     * 活动结束时调用
     */
    public void onActivityFinish(Long activityId) {
        log.info("Activity {} finished, Kafka Streams will continue processing", activityId);
        broadcastService.broadcastSystemMsg(String.valueOf(activityId), "{\"type\":\"activity_finished\"}");
    }
}
