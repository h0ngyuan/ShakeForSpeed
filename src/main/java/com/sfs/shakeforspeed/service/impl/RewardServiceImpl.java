package com.sfs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfs.mapper.RewardMapper;
import com.sfs.model.entity.Reward;
import com.sfs.model.vo.RewardVO;
import com.sfs.service.RewardService;
import com.sfs.utils.result.Result;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl extends ServiceImpl<RewardMapper,Reward> implements RewardService{

    @Override
    public Result acquireRewardsById(Long activityId) {
        List<RewardVO> vos = list(new LambdaQueryWrapper<Reward>()
                .eq(Reward::getActivityId, activityId)
                .orderByAsc(Reward::getRankRangeFront))
                .stream().map(reward -> {
                    RewardVO vo = new RewardVO();
                    vo.setId(reward.getId());
                    vo.setName(reward.getRewardName());
                    vo.setRankStart(reward.getRankRangeFront());
                    vo.setRankEnd(reward.getRankRangeEnd());
                    vo.setImageUrl(reward.getRewardImg());
                    return vo;
                }).collect(Collectors.toList());
        return Result.successResult(vos);
    }
}
