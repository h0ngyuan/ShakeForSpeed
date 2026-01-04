package com.sfs.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.sfs.model.enums.ActivityType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("activity")
public class Activity {

    /**
     * 主键 ID（自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动编号
     */
    @TableField("activity_no")
    private String activityNo;

    @TableField("activity_type")
    private ActivityType activityType;

    /**
     * 活动名称
     */
    @TableField("name")
    private String name;

    /**
     * 活动类型
     */
    @TableField("type")
    private String type;

    /**
     * 创建者 ID
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 创建者角色
     */
    @TableField("creator_role")
    private String creatorRole;

    /**
     * 活动状态（1:正常, 0:关闭）
     */
    @TableField("state")
    private Integer state;

    /**
     * 可见性（1:公开, 0:私密）
     */
    @TableField("visibility")
    private Integer visibility;

    /**
     * 房间代码（如会议号）
     */
    @TableField("room_code")
    private String roomCode;

    /**
     * 房间密码
     */
    @TableField("room_pwd")
    private String roomPwd;

    /**
     * 开始时间
     */
    @TableField("begin_time")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 持续时间（毫秒）
     */
    @TableField("duration_ms")
    private Long durationMs;

    /**
     * 最大参与人数
     */
    @TableField("max_participants")
    private Integer maxParticipants;

    /**
     * 当前参与人数
     */
    @TableField("current_participants")
    private Integer currentParticipants;

    /**
     * 活动描述（长文本）
     */
    @TableField("description")
    private String description;

    /**
     * 封面图片 URL
     */
    @TableField("cover_img")
    private String coverImg;

    /**
     * 地点名称
     */
    @TableField("location_name")
    private String locationName;

    /**
     * 经度（decimal(10,6)）
     */
    @TableField("lng")
    private BigDecimal lng;

    /**
     * 纬度（decimal(10,6)）
     */
    @TableField("lat")
    private BigDecimal lat;

    /**
     * 半径（米）
     */
    @TableField("radius_meters")
    private Integer radiusMeters;

    /**
     * 创建时间（自动填充）
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间（自动填充）
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}