package com.sfs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfs.common.exception.RedissonException;
import com.sfs.config.MinioConfig.MinioService;
import com.sfs.mapper.ActivityMapper;
import com.sfs.model.dto.ActivityInfoDTO;
import com.sfs.model.dto.CreateActivityDTO;
import com.sfs.model.dto.QueryActivityDTO;
import com.sfs.model.dto.RewardDTO;
import com.sfs.model.entity.Activity;
import com.sfs.model.entity.Reward;
import com.sfs.model.enums.ActivityState;
import com.sfs.model.enums.GroupConstants;
import com.sfs.model.enums.RedissonPrefixEnum;
import com.sfs.model.enums.TopicConstants;
import com.sfs.model.vo.ActivityDetailVO;
import com.sfs.model.vo.RewardVO;
import com.sfs.service.ActivityService;
import com.sfs.service.RewardService;
import com.sfs.utils.context.UserContext;
import com.sfs.utils.result.Result;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private MinioService minioService;
    @Autowired
    private RewardService rewardService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Override
    public Result queryActivities(QueryActivityDTO queryActivityDTO) {
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(queryActivityDTO.getActivityNameOrId()!=null,Activity::getId,queryActivityDTO.getActivityNameOrId())
                .or().like(queryActivityDTO.getActivityNameOrId()!=null,Activity::getActivityName,queryActivityDTO.getActivityNameOrId()) ;
        queryWrapper.eq(queryActivityDTO.getRole()!=null,Activity::getCreatorRole,queryActivityDTO.getRole());
        if (queryActivityDTO.getRangeTimeBefore()!=null&&queryActivityDTO.getRangeTimeAfter()!=null) {
            queryWrapper.between(Activity::getBeginTime,
                    (queryActivityDTO.getRangeTimeBefore().isBefore(queryActivityDTO.getRangeTimeAfter())?queryActivityDTO.getRangeTimeBefore():queryActivityDTO.getRangeTimeAfter()),
                    (queryActivityDTO.getRangeTimeBefore().isBefore(queryActivityDTO.getRangeTimeAfter())?queryActivityDTO.getRangeTimeBefore():queryActivityDTO.getRangeTimeAfter())
                    );
        }
        Page<Activity> page = new Page<>();
        page.setCurrent(queryActivityDTO.getPageIndex());
        page.setSize(queryActivityDTO.getPageSize());
        IPage<Activity> activityPages = activityMapper.selectPage(page,queryWrapper);
        return Result.successResult(activityPages);
    }

    @Override
    public void createActivity(CreateActivityDTO createActivityDTO) throws Exception {
        Activity activity = BeanUtil.copyProperties(createActivityDTO, Activity.class)
                .toBuilder()
                .creatorId(UserContext.getUser().getUserId())
                .creatorRole(UserContext.getUser().getRole())
                .activityType(Activity.ActivityType.SFS)
                .createTime(LocalDateTime.now())
//                .roomPwd(RandomUtil.randomInt(100000, 999999))
                .build();
        //房间号生成了得先查看当前redis有没有相同的，如果没有才能塞进去
        RLock lock = redissonClient.getLock(RedissonPrefixEnum.CREATE_ACTIVITY_PASSWORD_LOCK);
        int roomPwd = RandomUtil.randomInt(100000, 999999);
        if (lock.tryLock(5,10, TimeUnit.SECONDS)) {
            try {
                while (!redissonClient.getScoredSortedSet(RedissonPrefixEnum.ACTIVITY)
                        .valueRange(roomPwd, roomPwd).isEmpty()){
                    roomPwd=RandomUtil.randomInt(100000, 999999);
                }
            }finally {
                lock.unlock();
            }
        }else {
            throw new RedissonException("密码生成错误");
        }
        activity.setRoomPwd(roomPwd);
        save(activity);
        if (activity.getId()==null){
            throw new RuntimeException("保存失败");
        }
        List<RewardDTO> rewards = createActivityDTO.getRewards();
        for (RewardDTO reward : rewards) {
            String rewardImgUrl = reward.getImage()==null?null: minioService.uploadImage(reward.getImage(), "reward-img");
            Reward rw = Reward.builder()
                    .activityId(activity.getId())
                    .rewardName(reward.getName())
                    .rankRangeFront(reward.getRankStart())
                    .rankRangeEnd(reward.getRankEnd())
                    .rewardImg(rewardImgUrl)
                    .build();
            rewardService.save(rw);
        }

    }

    @Override
    public Result deleteById(Long id) {
        LambdaQueryWrapper<Reward> queryWrapper = new LambdaQueryWrapper<Reward>().eq(Reward::getActivityId, id);
        rewardService.list(queryWrapper)
                .stream()
                .map(Reward::getRewardImg)
                .forEach(imgUrl->
                    minioService.deleteFile(imgUrl));
        rewardService.remove(queryWrapper);
        return Result.successResult(activityMapper.deleteById(id));
    }

    @Override
    public Result getActivityById(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            return Result.failResult(404, "活动不存在");
        }
        // 查询关联的奖励数�?
        LambdaQueryWrapper<Reward> rewardQueryWrapper = new LambdaQueryWrapper<>();
        rewardQueryWrapper.eq(Reward::getActivityId, id).orderByAsc(Reward::getRankRangeFront);
        List<Reward> rewards = rewardService.list(rewardQueryWrapper);

        // 转换奖励数据为前端期望的格式
        List<RewardVO> rewardVOs = rewards.stream()
                .map(reward -> {
                    RewardVO vo = new RewardVO();
                    vo.setId(reward.getId());
                    vo.setName(reward.getRewardName());
                    vo.setRankStart(reward.getRankRangeFront());
                    vo.setRankEnd(reward.getRankRangeEnd());
                    vo.setImageUrl(reward.getRewardImg());
                    return vo;
                })
                .collect(Collectors.toList());

        // 转换活动数据为前端期望的格式
        ActivityDetailVO activityDetailVO = ActivityDetailVO.fromActivity(activity, rewardVOs);

        return Result.successResult(activityDetailVO);
    }

    /**
     * 加入这个房间
     *
     * @param activityInfoDTO
     * @return
     */
    @Override
    public Result joinActivity(ActivityInfoDTO activityInfoDTO) throws Exception {
        //先查看有没有房间号是这样的房间
        LambdaQueryWrapper<Activity> activityQueryWrapper = new LambdaQueryWrapper<Activity>().eq(Activity::getRoomPwd, activityInfoDTO.getRoomPwd())
                .in(Activity::getState, Arrays.asList(ActivityState.CREATED,ActivityState.PENDING));
        Activity activity = activityMapper.selectOne(activityQueryWrapper);
        if (activity==null&&activity.getRoomPwd()==null){
            throw new Exception("没有这样的房间");
        }
        //然后加入房间，上锁是为了防止了多设备情况
        String activityId = String.valueOf(activity.getId());
        String activity_activityId = RedissonPrefixEnum.ACTIVITY + activityId;
        String join_activityId = "JOIN"+activityId;
        RLock lock = redissonClient.getLock(join_activityId);
        if (lock.tryLock(3,3, TimeUnit.SECONDS)) {
            try {
                boolean contains = redissonClient.getScoredSortedSet(activity_activityId).contains(activityInfoDTO.getUserId());
                if (contains) {
                    throw new RedissonException("检测到用户已登入");
                }
                redissonClient.getScoredSortedSet(activity_activityId).add(0, RedissonPrefixEnum.USER+activityInfoDTO.getUserId());
            } finally {
                lock.unlock();
            }
        }
        Map<String,Object> args = null;
        args.put("TYPE","PeopleChange");
        args.put("METHOD","SUB");
        args.put("activityId",activityId);
        kafkaTemplate.send(TopicConstants.USER_CHANGE_INFO_TOPIC, GroupConstants.RANK_GROUP,args);
        return Result.successResult(activityId);

    }

    @Override
    public void exitActivity(ActivityInfoDTO activityInfoDTO) {
        //这边就退出房间
        String activity_activityId = RedissonPrefixEnum.ACTIVITY + activityInfoDTO.getActivityId();
        redissonClient.getScoredSortedSet(activity_activityId).remove(activityInfoDTO.getUserId());
        Map<String,Object> args = null;
        args.put("TYPE","PeopleChange");
        args.put("METHOD","SUB");
        args.put("activityId",activityInfoDTO.getActivityId());
        kafkaTemplate.send(TopicConstants.USER_CHANGE_INFO_TOPIC, GroupConstants.RANK_GROUP,args);
    }
}
