package com.sfs.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sfs.model.entity.Reward;
import com.sfs.service.RewardService;
import com.sfs.utils.result.Result;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("/reward")
@ResponseBody
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @PostMapping("/acquireRewards")
    public Result acquireRewards(@RequestParam("id") Long activityId){
        return rewardService.acquireRewardsById(activityId);

    }



}
