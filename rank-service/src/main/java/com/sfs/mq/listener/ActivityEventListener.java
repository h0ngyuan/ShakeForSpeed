package com.sfs.mq.listener;

import com.sfs.model.enums.GroupConstants;
import com.sfs.model.enums.RedissonPrefixEnum;
import com.sfs.model.enums.TopicConstants;
import com.sfs.service.RoomService;
import com.sfs.websocket.service.BroadcastService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ActivityEventListener {

    @Autowired
    private RoomService roomService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private BroadcastService broadcastService;


    @KafkaListener(topics = TopicConstants.USER_CHANGE_INFO_TOPIC,
                    groupId = GroupConstants.PARTICIPATE_GROUP)
    public void PeopleCountChangeEvent(Map<String,Object> info){
        String activityId = (String) info.get("activityId");
        //info可以写入日志，但是我暂时没这打算
        int count = redissonClient.getScoredSortedSet(RedissonPrefixEnum.ACTIVITY + activityId)
                .size();
        info.put("COUNT",count);
        broadcastService.broadcast(activityId,info);
    }

    @KafkaListener(topics = TopicConstants.ACTIVITY_START_TOPIC,
    groupId = GroupConstants.PARTICIPATE_GROUP)
    public void ActivityStartEvent(Map<String,Object> info){
        String activityId = (String) info.get("activityId");
        broadcastService.broadcast(activityId,info);
    }

}
