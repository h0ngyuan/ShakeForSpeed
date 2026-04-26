package com.sfs.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Slf4j
@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic shakeEventsTopic() {
        return TopicBuilder.name("shake-events")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic activityEventsTopic() {
        return TopicBuilder.name("activity-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic rewardGrantsTopic() {
        return TopicBuilder.name("reward-grants")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic shakeRanksTopic() {
        return TopicBuilder.name("shake-ranks")
                .partitions(6)
                .replicas(1)
                .build();
    }
}
