package com.sfs.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sfs.entity.Activity;
import com.sfs.enums.ActivityState;
import com.sfs.mapper.ActivityMapper;
import com.sfs.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityScheduler {

    private final ActivityMapper activityMapper;
    private final RewardService rewardService;

    @Scheduled(fixedRate = 60000)
    public void checkAutoPublish() {
        LocalDateTime now = LocalDateTime.now();
        List<Activity> activities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .eq(Activity::getState, ActivityState.PENDING.getCode())
                        .le(Activity::getBeginTime, now)
        );
        for (Activity activity : activities) {
            activity.setState(ActivityState.RUNNING.getCode());
            activity.setEndTime(now.plusSeconds(activity.getDurationMs() / 1000));
            activityMapper.updateById(activity);
            log.info("Activity {} auto started", activity.getId());
        }
    }

    @Scheduled(fixedRate = 60000)
    public void checkAutoFinish() {
        LocalDateTime now = LocalDateTime.now();
        List<Activity> activities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .eq(Activity::getState, ActivityState.RUNNING.getCode())
                        .le(Activity::getEndTime, now)
        );
        for (Activity activity : activities) {
            activity.setState(ActivityState.FINISHED.getCode());
            activityMapper.updateById(activity);
            rewardService.grantRewards(activity.getId());
            log.info("Activity {} auto finished, rewards granted", activity.getId());
        }
    }
}
