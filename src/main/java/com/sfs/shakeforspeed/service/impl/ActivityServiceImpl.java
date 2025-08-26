package com.sfs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfs.config.MinioConfig.MinioService;
import com.sfs.mapper.ActivityMapper;
import com.sfs.model.dto.ActivityDetailDTO;
import com.sfs.model.dto.CreateActivityDTO;
import com.sfs.model.dto.QueryActivityDTO;
import com.sfs.model.dto.RewardDTO;
import com.sfs.model.entity.Activity;
import com.sfs.model.entity.Reward;
import com.sfs.model.vo.ActivityDetailVO;
import com.sfs.model.vo.RewardVO;
import com.sfs.service.ActivityService;
import com.sfs.service.RewardService;
import com.sfs.utils.context.UserContext;
import com.sfs.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper,Activity> implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private MinioService minioService;
    @Autowired
    private RewardService rewardService;


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
                .roomPwd(RandomUtil.randomInt(100000, 999999))
                .build();
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
                .forEach(imgUrl->{
                    minioService.deleteFile(imgUrl);
                });
        rewardService.remove(queryWrapper);
        return Result.successResult(activityMapper.deleteById(id));
    }
    
    @Override
    public Result getActivityById(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            return Result.failResult(404, "活动不存�?);
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
}
