package com.sfs.model.vo;

import com.sfs.model.entity.Activity;
import com.sfs.model.enums.ActivityState;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityDetailVO {
    private Long id;
    private ActivityState state;
    private String activityName;
    private String activityType;
    private String creatorRole;
    private LocalDateTime beginTime;
    private LocalDateTime createTime;
    private Long creatorId;
    private Long durTime;
    private String roomPwd;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private List<RewardVO> rewards;

    public static ActivityDetailVO fromActivity(Activity activity, List<RewardVO> rewards) {
        ActivityDetailVO vo = new ActivityDetailVO();
        vo.setId(activity.getId());
        vo.setActivityName(activity.getName());
        vo.setActivityType(activity.getActivityType().name());
        vo.setCreatorRole(activity.getCreatorRole());
        vo.setBeginTime(activity.getBeginTime());
        vo.setCreateTime(activity.getBeginTime());
        vo.setCreatorId(activity.getCreatorId());
        vo.setDurTime(activity.getDurationMs());
        vo.setRoomPwd(activity.getRoomPwd());
        vo.setLongitude(activity.getLng());
        vo.setLatitude(activity.getLat());
        vo.setRewards(rewards);
        return vo;
    }
}
