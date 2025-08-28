package com.sfs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 全局CORS配置
 * 统一处理跨域请求，支持前端开发和生产环境
 */
@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的源（开发环境常用端口）
        config.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",
            "https://localhost:*",
            "http://127.0.0.1:*",
            "https://127.0.0.1:*",
            // 添加生产环境域名
            "https://yourdomain.com",
            "https://*.yourdomain.com"
        ));
        
        // 允许的HTTP方法
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        
        // 允许的请求头
        config.setAllowedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization", 
            "token",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-User-Id",
            "X-Username", 
            "X-User-Role"
        ));
        
        // 暴露的响应头
        config.setExposedHeaders(Arrays.asList(
            "Content-Length",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Cache-Control",
            "Content-Language",
            "Content-Type",
            "X-User-Id",
            "X-Username",
            "X-User-Role"
        ));
        
        // 允许发送凭证（cookies）
        config.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用此配置
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}