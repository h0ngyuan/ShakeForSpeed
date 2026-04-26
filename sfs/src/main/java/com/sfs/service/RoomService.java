package com.sfs.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sfs.dto.JoinRoomDTO;
import com.sfs.entity.Activity;
import com.sfs.enums.ActivityState;
import com.sfs.enums.AppHttpCodeEnum;
import com.sfs.exception.BusinessException;
import com.sfs.mapper.ActivityMapper;
import com.sfs.vo.RoomStatusVO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final ActivityMapper activityMapper;
    private final RedissonClient redissonClient;

    public RoomStatusVO joinRoom(Long userId, JoinRoomDTO dto) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getRoomCode, dto.getRoomCode());
        Activity activity = activityMapper.selectOne(wrapper);
        if (activity == null) {
            throw new BusinessException(AppHttpCodeEnum.ROOM_CODE_INVALID);
        }
        if (!activity.getRoomPwd().equals(dto.getRoomPwd())) {
            throw new BusinessException(AppHttpCodeEnum.ROOM_PWD_INVALID);
        }
        if (activity.getState() != ActivityState.RUNNING.getCode()) {
            throw new BusinessException(AppHttpCodeEnum.ACTIVITY_NOT_RUNNING, "活动未进行中");
        }
        RSet<String> users = redissonClient.getSet("room:users:" + activity.getId());
        if (users.contains(String.valueOf(userId))) {
            return getRoomStatus(activity.getRoomCode());
        }
        if (users.size() >= activity.getMaxParticipants()) {
            throw new BusinessException(AppHttpCodeEnum.ROOM_FULL);
        }
        users.add(String.valueOf(userId));
        RBucket<Long> bucket = redissonClient.getBucket("room:online:" + activity.getRoomCode());
        Long count = bucket.get();
        if (count == null) count = 0L;
        bucket.set(count + 1);
        return getRoomStatus(activity.getRoomCode());
    }

    public void leaveRoom(Long userId, Long activityId) {
        RSet<String> users = redissonClient.getSet("room:users:" + activityId);
        users.remove(String.valueOf(userId));
        Activity activity = activityMapper.selectById(activityId);
        if (activity != null) {
            RBucket<Long> bucket = redissonClient.getBucket("room:online:" + activity.getRoomCode());
            Long count = bucket.get();
            if (count != null && count > 0) {
                bucket.set(count - 1);
            }
        }
    }

    public RoomStatusVO getRoomStatus(String roomCode) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getRoomCode, roomCode);
        Activity activity = activityMapper.selectOne(wrapper);
        if (activity == null) {
            throw new BusinessException(AppHttpCodeEnum.ROOM_CODE_INVALID);
        }
        RoomStatusVO vo = new RoomStatusVO();
        vo.setRoomCode(roomCode);
        vo.setActivityName(activity.getName());
        vo.setState(activity.getState());
        vo.setMaxParticipants(activity.getMaxParticipants().longValue());
        RBucket<Long> bucket = redissonClient.getBucket("room:online:" + roomCode);
        Long count = bucket.get();
        vo.setOnlineCount(count != null ? count : 0L);
        return vo;
    }
}
