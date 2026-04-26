package com.sfs.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class ShakeEventStreamTopology {

    @Value("${app.kafka.topics.shake-events:shake-events}")
    private String shakeEventsTopic;

    @Value("${app.kafka.topics.shake-ranks:shake-ranks}")
    private String shakeRanksTopic;

    /**
     * 定义 Kafka Streams 流式处理拓扑
     * Spring Boot 会自动检测 StreamsBuilder bean 并启动 Streams
     * 流程：读取原始事件 → 按 activityId-userId 分组 → 1秒窗口聚合 → 输出到 shake-ranks topic
     */
    @Bean
    public StreamsBuilder streamsBuilder() {
        StreamsBuilder builder = new StreamsBuilder();

        // 1. 读取原始摇动事件
        KStream<String, String> source = builder.stream(shakeEventsTopic,
                Consumed.with(Serdes.String(), Serdes.String()));

        // 2. 解析消息并提取 key (activityId-userId)
        KStream<String, String> keyedStream = source
                .filter((key, value) -> value != null && value.contains(","))
                .map((key, value) -> {
                    String[] parts = value.split(",");
                    if (parts.length >= 4) {
                        String activityId = parts[0];
                        String userId = parts[1];
                        return KeyValue.pair(activityId + "-" + userId, value);
                    }
                    return KeyValue.pair("invalid", value);
                })
                .filter((key, value) -> !key.equals("invalid"));

        // 3. 使用 1 秒滚动窗口聚合计数
        keyedStream
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(1)))
                .count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("shake-count-store")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long()))
                .toStream()
                // 4. 格式化输出到 shake-ranks topic
                .map((windowedKey, count) -> {
                    String[] parts = windowedKey.key().split("-");
                    String activityId = parts[0];
                    String userId = parts[1];
                    String value = String.format("%s,%s,%d,%d",
                            activityId, userId, count, System.currentTimeMillis());
                    return KeyValue.pair(activityId, value);
                })
                .to(shakeRanksTopic, Produced.with(Serdes.String(), Serdes.String()));

        log.info("Kafka Streams topology initialized: {} -> {}", shakeEventsTopic, shakeRanksTopic);
        return builder;
    }
}
