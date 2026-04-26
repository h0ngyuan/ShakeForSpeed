package com.sfs.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.sfs.service.RewardService;
import com.sfs.util.Result;
import com.sfs.vo.RewardConfigVO;
import com.sfs.vo.RewardGrantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @GetMapping("/{activityId}")
    public Result<List<RewardConfigVO>> getConfig(@PathVariable Long activityId) {
        return Result.success(rewardService.getRewardConfig(activityId));
    }

    @PostMapping("/{grantId}/claim")
    public Result<Void> claim(@PathVariable Long grantId) {
        Long userId = StpUtil.getLoginIdAsLong();
        rewardService.claimReward(grantId, userId);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<RewardGrantVO>> myRewards() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(rewardService.getMyRewards(userId));
    }
}
