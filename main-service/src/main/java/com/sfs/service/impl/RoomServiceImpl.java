package com.sfs.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sfs.model.entity.Activity;
import com.sfs.model.enums.ActivityState;
import com.sfs.model.enums.AppHttpCodeEnum;
import com.sfs.service.ActivityService;
import com.sfs.service.RoomService;
import com.sfs.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.sfs.model.enums.ActivityState.CREATED;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Override
    public Result startActivity(Long activityId) throws Exception {
        //查并改数据库状态
        Activity activity = activityService.getById(activityId);
        switch (activity.getState()){
            case CREATED -> activityService.update(activity,new UpdateWrapper<Activity>()
                    .eq("state", ActivityState.ACTIVE));
            default -> throw new Exception("已经点过了");
        }
        
        //Kafka通知所有小程序端ws
        Map<String,Object> args = new HashMap<>();
        args.put("TYPE","START");
        args.put("activityId",activityId);
        kafkaTemplate.send(String.valueOf(activityId),args);
        return Result.successResult(AppHttpCodeEnum.SUCCESS);
    }



}
