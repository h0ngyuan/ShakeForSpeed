package com.sfs.topology;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sfs.model.entity.UserShakeActionEvent;
import com.sfs.model.enums.TopicConstants;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableKafkaStreams
public class ShakeStreamTopology {

    @Bean
    public KStream<String,String> shakeCountForStream(StreamsBuilder builder){
        KStream<String, String> source = builder.stream(TopicConstants.SHAKE_FOR_SPEED_RAW);
        KStream<String,UserShakeActionEvent> keyed = source.map(
                (key,value)->{
                    UserShakeActionEvent event = JSONUtil.toBean(value, UserShakeActionEvent.class);
                    return new KeyValue<>(event.getActivityId(),event);
                });
        KGroupedStream<String, UserShakeActionEvent> grouped = keyed.groupByKey();
        TimeWindows window = TimeWindows.of(Duration.of(1, ChronoUnit.SECONDS)).advanceBy(Duration.ofSeconds(1));

        KTable<Windowed<String>, Long> totalShakes = grouped.windowedBy(window)
                .aggregate(
                        () -> 0L,
                        (key, event, aggregate) ->
                                aggregate + event.getCount()
                );
        totalShakes.toStream()
                .map((windowedKey,total)->{
                    String activityId = windowedKey.key();
                    String output = JSONUtil.toJsonStr(Map.of(
                            "activity", activityId,
                            "totalShakes", total
                    ));
                    return new KeyValue<>(activityId,output);
                }).to(
                        TopicConstants.SHAKE_FOR_SPEED_PROCESSED,
                Produced.valueSerde(Serdes.String()));
        return source;
    }



}
