package com.sfs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateActivityDTO {
    @NotBlank(message = "活动名称不能为空")
    private String name;
    private String type;
    private String description;
    private String coverImg;
    private Long durationMs;
    private Integer maxParticipants;
    private String locationName;
    private Double lng;
    private Double lat;
    private java.time.LocalDateTime beginTime;
}
