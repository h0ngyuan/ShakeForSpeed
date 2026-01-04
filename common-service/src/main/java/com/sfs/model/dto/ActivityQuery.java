package com.sfs.model.dto;

import com.sfs.model.entity.Page;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ActivityQuery extends Page {
    private Long id;
    private String activityNo;
    private String name;
    private String type;
    private Long creatorId;
    private String creatorRole;
    /**
     * 活动状态（1:正常, 0:关闭）
     */
    private Integer state;

    /**
     * 可见性（1:公开, 0:私密）
     */
    private Integer visibility;

    /**
     * 房间代码（如会议号）
     */
    private String roomCode;

    /**
     * 房间密码
     */
    private String roomPwd;

    /**
     * 开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 持续时间（毫秒）
     */
    private Long durationMs;

    /**
     * 最大参与人数
     */
    private Integer maxParticipants;

    /**
     * 当前参与人数
     */
    private Integer currentParticipants;

    /**
     * 活动描述（长文本）
     */
    private String description;

    /**
     * 封面图片 URL
     */
    private String coverImg;

    /**
     * 地点名称
     */
    private String locationName;

    /**
     * 经度（decimal(10,6)）
     */
    private BigDecimal lng;

    /**
     * 纬度（decimal(10,6)）
     */
    private BigDecimal lat;

    /**
     * 半径（米）
     */
    private Integer radiusMeters;

    /**
     * 创建时间（自动填充）
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间（自动填充）
     */
    private LocalDateTime updatedAt;

}