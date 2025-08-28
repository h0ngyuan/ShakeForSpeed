package com.sfs.websocket.engine;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.stream-engine", havingValue = "kafka-streams")
public class KafkaStreamShakeEngine implements ShakeCountEngine{
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Long getTotalShakes(Long activityId) {
        return 0L;
    }
}
