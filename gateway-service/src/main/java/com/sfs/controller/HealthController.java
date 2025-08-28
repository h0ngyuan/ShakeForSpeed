package com.sfs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 网关健康检查控制器
 * 提供网关服务状态检查接口
 */
@RestController
@RequestMapping("/gateway")
public class HealthController {

    @GetMapping("/health")
    public Mono<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "gateway-service");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Gateway service is running normally");
        return Mono.just(response);
    }

    @GetMapping("/info")
    public Mono<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "gateway-service");
        response.put("version", "1.0.0");
        response.put("description", "API Gateway for ShakeForSpeed project");
        response.put("features", new String[]{
            "JWT Authentication",
            "CORS Support", 
            "Rate Limiting",
            "Route Management",
            "Global Exception Handling"
        });
        response.put("timestamp", LocalDateTime.now());
        return Mono.just(response);
    }
}