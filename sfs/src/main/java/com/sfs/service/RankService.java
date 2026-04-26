package com.sfs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

    private final RedissonClient redissonClient;

    public List<com.sfs.vo.RankItemVO> getRealtimeRank(Long activityId, int limit) {
        String key = "rank:" + activityId;
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(key);
        Collection<String> entries = sortedSet.valueRangeReversed(0, limit - 1);
        List<com.sfs.vo.RankItemVO> result = new ArrayList<>();
        int rank = 1;
        for (String userId : entries) {
            Double score = sortedSet.getScore(userId);
            com.sfs.vo.RankItemVO item = new com.sfs.vo.RankItemVO();
            item.setRank(rank++);
            item.setUserId(userId);
            item.setScore(score != null ? score.longValue() : 0L);
            item.setIsCheat(false);
            result.add(item);
        }
        return result;
    }

    public void addScore(Long activityId, String userId, int score) {
        String key = "rank:" + activityId;
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(key);
        Double currentScore = sortedSet.getScore(userId);
        double newScore = (currentScore != null ? currentScore : 0) + score;
        sortedSet.addScore(userId, newScore);
    }

    public void updateScore(Long activityId, String userId, int score) {
        String key = "rank:" + activityId;
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(key);
        sortedSet.addScore(userId, score);
    }

    public void clearRank(Long activityId) {
        String key = "rank:" + activityId;
        redissonClient.getScoredSortedSet(key).delete();
    }
}
