package com.sfs.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 简单的请求过滤器
 * 用于基本的请求过滤，不处理Token验证
 */
@Component
public class SimpleRequestFilter extends AbstractGatewayFilterFactory<SimpleRequestFilter.Config> {

    // 不需要特殊处理的路径列表
    private static final List<String> SKIP_PATHS = Arrays.asList(
            "/actuator",
            "/gateway",
            "/favicon.ico"
    );

    public SimpleRequestFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            
            // 简单的请求日志
            System.out.println("Request received: " + path);
            
            // 可以在这里添加额外的请求过滤逻辑
            // 如黑名单IP检查、请求频率限制等
            
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // 配置类，目前为空
    }
}