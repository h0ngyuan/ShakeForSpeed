package com.sfs.flink;

import com.sfs.config.FlinkConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlinkJobManager {

    private final FlinkConfig flinkConfig;

    @PostConstruct
    public void init() {
        if (flinkConfig.isEmbedded()) {
            log.info("Flink embedded mode enabled. Flink jobs will start on demand.");
        } else {
            log.info("Flink remote mode. Connect to external Flink cluster.");
        }
    }

    public void startJob(Long activityId) {
        log.info("Starting Flink job for activity: {}", activityId);
    }

    public void stopJob(Long activityId) {
        log.info("Stopping Flink job for activity: {}", activityId);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down Flink JobManager");
    }
}
