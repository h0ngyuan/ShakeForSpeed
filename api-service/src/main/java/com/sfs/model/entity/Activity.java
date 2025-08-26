package com.sfs.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.kafka.common.protocol.types.Field;

import java.time.LocalDateTime;

@TableName("activity")
@Data
@Builder(toBuilder = true)
public class Activity {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String activityName;
    private ActivityType activityType;
    private String creatorRole;
    private LocalDateTime beginTime;
    private LocalDateTime createTime;
    private Long creatorId;
    private String rewardName;
    private String rewardId;
    private Long durTime;
    private Integer roomPwd;
    private Double longitude;
    private Double latitude;


    @Getter
    public enum ActivityType{
        SFS(1,"拼手速摇一摇"),
        SFR(2,"拼手速抢红包");
        int activityTypeId;
        String activityTypeName;
        ActivityType(int activityTypeId, String activityTypeName){
            this.activityTypeId=activityTypeId;
            this.activityTypeName=activityTypeName;
        }
    }
}
