package com.sfs.config;

import com.sfs.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关路由配置
 * 配置各个微服务的路由规则和过滤器
 * CORS由GlobalCorsConfig统一处理
 */
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        return builder.routes()
                // API服务路由
                .route("api-service", r -> r
                        .path("/api/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8001"))
                
                // 主服务路由
                .route("main-service", r -> r
                        .path("/main/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8002"))
                
                // 排名服务路由 (HTTP API)
                .route("rank-service", r -> r
                        .path("/rank/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8080"))
                
                // 健康检查路由（不需要认证）
                .route("health-check", r -> r
                        .path("/actuator/**")
                        .uri("http://localhost:9999"))
                
                // 网关本身的健康检查（不需要认证）
                .route("gateway-health", r -> r
                        .path("/gateway/**")
                        .uri("http://localhost:9999"))
                
                .build();
    }
}