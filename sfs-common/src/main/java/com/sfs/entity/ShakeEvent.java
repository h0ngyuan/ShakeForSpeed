package com.sfs.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShakeEvent {
    private String id;
    private String userId;
    private String activityId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;
    private Integer intensity;
    private DeviceInfo deviceInfo;
    private LocationInfo location;

    @Data
    public static class DeviceInfo {
        private String model;
        private String os;
        private String appVersion;
    }

    @Data
    public static class LocationInfo {
        private String type;
        private Double[] coordinates;
    }
}
