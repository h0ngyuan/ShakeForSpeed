package com.sfs.model.vo;

import com.sfs.model.dto.ActivityDetailDTO;
import com.sfs.model.dto.RewardDTO;
import com.sfs.model.entity.Activity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityDetailVO {
    private Long id;
    private String activityName;
    private String activityType;
    private String creatorRole;
    private LocalDateTime beginTime;
    private LocalDateTime createTime;
    private Long creatorId;
    private Long durTime;
    private Integer roomPwd;
    private Double longitude;
    private Double latitude;
    private List<RewardVO> rewards;

    public static ActivityDetailVO fromActivity(Activity activity, List<RewardVO> rewards) {
        ActivityDetailVO vo = new ActivityDetailVO();
        vo.setId(activity.getId());
        vo.setActivityName(activity.getActivityName());
        vo.setActivityType(activity.getActivityType().name());
        vo.setCreatorRole(activity.getCreatorRole());
        vo.setBeginTime(activity.getBeginTime());
        vo.setCreateTime(activity.getCreateTime());
        vo.setCreatorId(activity.getCreatorId());
        vo.setDurTime(activity.getDurTime());
        vo.setRoomPwd(activity.getRoomPwd());
        vo.setLongitude(activity.getLongitude());
        vo.setLatitude(activity.getLatitude());
        vo.setRewards(rewards);
        return vo;
    }
}
