package com.sfs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sfs.model.entity.Reward;
import com.sfs.utils.result.Result;

public interface RewardService extends IService<Reward> {
    Result acquireRewardsById(Long activityId);
}
