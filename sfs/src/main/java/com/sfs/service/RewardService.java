package com.sfs.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sfs.entity.Leaderboard;
import com.sfs.entity.Reward;
import com.sfs.entity.RewardGrant;
import com.sfs.enums.AppHttpCodeEnum;
import com.sfs.exception.BusinessException;
import com.sfs.mapper.LeaderboardMapper;
import com.sfs.mapper.RewardGrantMapper;
import com.sfs.mapper.RewardMapper;
import com.sfs.vo.RewardGrantVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardMapper rewardMapper;
    private final LeaderboardMapper leaderboardMapper;
    private final RewardGrantMapper rewardGrantMapper;

    public List<com.sfs.vo.RewardConfigVO> getRewardConfig(Long activityId) {
        LambdaQueryWrapper<Reward> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reward::getActivityId, activityId).orderByAsc(Reward::getSortOrder);
        return rewardMapper.selectList(wrapper).stream().map(this::toConfigVO).toList();
    }

    public List<RewardGrantVO> getMyRewards(Long userId) {
        LambdaQueryWrapper<RewardGrant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RewardGrant::getUserId, userId)
               .orderByDesc(RewardGrant::getGrantTime);
        return rewardGrantMapper.selectList(wrapper).stream().map(this::toGrantVO).toList();
    }

    @Transactional
    public void grantRewards(Long activityId) {
        List<Leaderboard> leaders = leaderboardMapper.selectList(
                new LambdaQueryWrapper<Leaderboard>().eq(Leaderboard::getActivityId, activityId).orderByAsc(Leaderboard::getRank)
        );
        LambdaQueryWrapper<Reward> rewardWrapper = new LambdaQueryWrapper<>();
        rewardWrapper.eq(Reward::getActivityId, activityId).orderByAsc(Reward::getSortOrder);
        List<Reward> rewards = rewardMapper.selectList(rewardWrapper);

        int grantedCount = 0;
        for (Leaderboard lb : leaders) {
            for (Reward reward : rewards) {
                if (lb.getRank() >= reward.getRankStart() && lb.getRank() <= reward.getRankEnd()) {
                    if (grantToUser(lb.getUserId(), activityId, reward.getId(), lb.getRank())) {
                        grantedCount++;
                    }
                    break;
                }
            }
        }
        log.info("Granted {} rewards for activity {}", grantedCount, activityId);
    }

    @Transactional
    public boolean grantToUser(Long userId, Long activityId, Long rewardConfigId, Integer rank) {
        LambdaQueryWrapper<RewardGrant> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(RewardGrant::getUserId, userId)
                    .eq(RewardGrant::getActivityId, activityId)
                    .eq(RewardGrant::getRewardConfigId, rewardConfigId);
        if (rewardGrantMapper.selectCount(checkWrapper) > 0) {
            log.debug("Reward already granted: userId={}, activityId={}, rewardId={}", userId, activityId, rewardConfigId);
            return false;
        }

        RewardGrant grant = new RewardGrant();
        grant.setUserId(userId);
        grant.setActivityId(activityId);
        grant.setRewardConfigId(rewardConfigId);
        grant.setRank(rank);
        grant.setGrantStatus(1);
        grant.setGrantTime(LocalDateTime.now());
        rewardGrantMapper.insert(grant);
        log.info("Reward granted: userId={}, activityId={}, rank={}", userId, activityId, rank);
        return true;
    }

    @Transactional
    public void claimReward(Long grantId, Long userId) {
        RewardGrant grant = rewardGrantMapper.selectById(grantId);
        if (grant == null) {
            throw new BusinessException(AppHttpCodeEnum.BAD_REQUEST, "奖励记录不存在");
        }
        if (!grant.getUserId().equals(userId)) {
            throw new BusinessException(AppHttpCodeEnum.BAD_REQUEST, "无权领取此奖励");
        }
        if (grant.getGrantStatus() != 1) {
            throw new BusinessException(AppHttpCodeEnum.BAD_REQUEST, "奖励不可领取");
        }
        grant.setClaimStatus(1);
        grant.setClaimTime(LocalDateTime.now());
        rewardGrantMapper.updateById(grant);
    }

    private com.sfs.vo.RewardConfigVO toConfigVO(Reward r) {
        com.sfs.vo.RewardConfigVO vo = new com.sfs.vo.RewardConfigVO();
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

    private RewardGrantVO toGrantVO(RewardGrant g) {
        RewardGrantVO vo = new RewardGrantVO();
        vo.setId(g.getId());
        vo.setActivityId(g.getActivityId());
        vo.setRewardConfigId(g.getRewardConfigId());
        vo.setRank(g.getRank());
        vo.setGrantStatus(g.getGrantStatus());
        vo.setClaimStatus(g.getClaimStatus());
        vo.setGrantTime(g.getGrantTime());
        vo.setClaimTime(g.getClaimTime());
        return vo;
    }
}
