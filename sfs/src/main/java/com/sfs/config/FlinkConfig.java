package com.sfs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.flink")
public class FlinkConfig {
    private boolean embedded = true;
    private long checkpointInterval = 10000;
    private String jobName = "shake-count-job";
}
