package com.sfs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sfs.dto.ActivityQueryDTO;
import com.sfs.dto.CreateActivityDTO;
import com.sfs.dto.RewardConfigDTO;
import com.sfs.entity.Activity;
import com.sfs.entity.Reward;
import com.sfs.enums.ActivityState;
import com.sfs.enums.AppHttpCodeEnum;
import com.sfs.exception.BusinessException;
import com.sfs.mapper.ActivityMapper;
import com.sfs.mapper.RewardMapper;
import com.sfs.util.RoomCodeGenerator;
import com.sfs.util.SnowflakeIdGenerator;
import com.sfs.vo.ActivityDetailVO;
import com.sfs.vo.ActivityVO;
import com.sfs.entity.PageResult;
import com.sfs.vo.RewardConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityMapper activityMapper;
    private final RewardMapper rewardMapper;

    @Transactional
    public ActivityVO createActivity(Long creatorId, CreateActivityDTO dto) {
        Activity activity = new Activity();
        activity.setActivityNo(SnowflakeIdGenerator.getInstance().nextUid());
        activity.setName(dto.getName());
        activity.setType(dto.getType() != null ? dto.getType() : "SHAKE_COUNT");
        activity.setCreatorId(creatorId);
        activity.setState(ActivityState.DRAFT.getCode());
        activity.setRoomCode(RoomCodeGenerator.generate());
        activity.setRoomPwd(RoomCodeGenerator.generatePassword());
        activity.setDurationMs(dto.getDurationMs() != null ? dto.getDurationMs() : 120000L);
        activity.setMaxParticipants(dto.getMaxParticipants() != null ? dto.getMaxParticipants() : 400);
        activity.setDescription(dto.getDescription());
        activity.setCoverImg(dto.getCoverImg());
        activity.setLocationName(dto.getLocationName());
        activity.setLng(dto.getLng());
        activity.setLat(dto.getLat());
        activity.setBeginTime(dto.getBeginTime());
        activityMapper.insert(activity);
        return toVO(activity);
    }

    public ActivityVO getActivity(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException(AppHttpCodeEnum.ACTIVITY_NOT_FOUND);
        }
        return toVO(activity);
    }

    public PageResult<ActivityVO> queryActivities(ActivityQueryDTO dto) {
        Page<Activity> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (dto.getState() != null) {
            wrapper.eq(Activity::getState, dto.getState());
        }
        if (dto.getCreatorId() != null) {
            wrapper.eq(Activity::getCreatorId, dto.getCreatorId());
        }
        wrapper.orderByDesc(Activity::getCreatedAt);
        Page<Activity> result = activityMapper.selectPage(page, wrapper);
        List<ActivityVO> records = result.getRecords().stream().map(this::toVO).toList();
        return PageResult.of(result.getTotal(), dto.getPage(), dto.getSize(), records);
    }

    public ActivityDetailVO getDetail(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException(AppHttpCodeEnum.ACTIVITY_NOT_FOUND);
        }
        ActivityDetailVO vo = new ActivityDetailVO();
        vo.setId(activity.getId());
        vo.setActivityNo(activity.getActivityNo());
        vo.setName(activity.getName());
        vo.setType(activity.getType());
        vo.setState(activity.getState());
        vo.setRoomCode(activity.getRoomCode());
        vo.setRoomPwd(activity.getRoomPwd());
        vo.setDescription(activity.getDescription());
        vo.setCoverImg(activity.getCoverImg());
        vo.setLocationName(activity.getLocationName());
        vo.setLng(activity.getLng());
        vo.setLat(activity.getLat());
        vo.setBeginTime(activity.getBeginTime());
        vo.setEndTime(activity.getEndTime());
        vo.setDurationMs(activity.getDurationMs());
        vo.setMaxParticipants(activity.getMaxParticipants());
        LambdaQueryWrapper<Reward> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reward::getActivityId, id).orderByAsc(Reward::getSortOrder);
        List<RewardConfigVO> rewards = rewardMapper.selectList(wrapper).stream().map(this::toRewardVO).toList();
        vo.setRewards(rewards);
        return vo;
    }

    @Transactional
    public void publishActivity(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException(AppHttpCodeEnum.ACTIVITY_NOT_FOUND);
        }
        activity.setState(ActivityState.PENDING.getCode());
        activityMapper.updateById(activity);
    }

    @Transactional
    public void finishActivity(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException(AppHttpCodeEnum.ACTIVITY_NOT_FOUND);
        }
        activity.setState(ActivityState.FINISHED.getCode());
        activityMapper.updateById(activity);
    }

    private ActivityVO toVO(Activity a) {
        ActivityVO vo = new ActivityVO();
        vo.setId(a.getId());
        vo.setActivityNo(a.getActivityNo());
        vo.setName(a.getName());
        vo.setType(a.getType());
        vo.setState(a.getState());
        vo.setRoomCode(a.getRoomCode());
        vo.setDescription(a.getDescription());
        vo.setCoverImg(a.getCoverImg());
        vo.setBeginTime(a.getBeginTime());
        vo.setEndTime(a.getEndTime());
        return vo;
    }

    private RewardConfigVO toRewardVO(Reward r) {
        RewardConfigVO vo = new RewardConfigVO();
        vo.setId(r.getId());
        vo.setRankStart(r.getRankStart());
        vo.setRankEnd(r.getRankEnd());
        vo.setRewardType(r.getRewardType());
        vo.setRewardName(r.getRewardName());
        vo.setRewardValue(r.getRewardValue());
        vo.setRewardImg(r.getRewardImg());
        vo.setQuantity(r.getQuantity());
        return vo;
    }
}
