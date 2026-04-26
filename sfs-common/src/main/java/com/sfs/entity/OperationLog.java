package com.sfs.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLog {
    private String id;
    private String operatorId;
    private String operatorRole;
    private String action;
    private String targetType;
    private String targetId;
    private Object request;
    private Object response;
    private String ipAddress;
    private String userAgent;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    private Integer durationMs;
}
