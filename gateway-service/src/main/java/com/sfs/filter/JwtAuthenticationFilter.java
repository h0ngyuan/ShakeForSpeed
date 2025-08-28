package com.sfs.filter;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器
 * 用于验证请求中的JWT Token并进行权限校验
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // 不需要认证的路径列表
    private static final List<String> SKIP_AUTH_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register", 
            "/main/auth/login",
            "/main/auth/register",
            "/rank/test",
            "/actuator",
            "/gateway",
            "/favicon.ico"
    );

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            
            // 检查是否是跳过认证的路径
            if (shouldSkipAuth(path)) {
                return chain.filter(exchange);
            }

            // 获取Authorization header
            String authHeader = request.getHeaders().getFirst("token");
            
            if (!StringUtils.hasText(authHeader)) {
                return handleAuthenticationError(exchange, "Missing authentication token");
            }

            // 移除Bearer_前缀
            String token = authHeader.replaceFirst("^Bearer_", "");
            
            // 验证JWT token
            try {
                if (!JWTUtil.verify(token, jwtSecret.getBytes())) {
                    return handleAuthenticationError(exchange, "Invalid token");
                }

                // 解析token获取用户信息
                JWT jwt = JWTUtil.parseToken(token);
                JWTPayload payload = jwt.getPayload();
                String userId = payload.getClaim("userId").toString();
                String username = payload.getClaim("username").toString();
                String role = payload.getClaim("role").toString();

                // 在请求头中添加用户信息，传递给下游服务
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-Username", username)
                        .header("X-User-Role", role)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                return handleAuthenticationError(exchange, "Token verification failed: " + e.getMessage());
            }
        };
    }

    /**
     * 检查是否应该跳过认证
     */
    private boolean shouldSkipAuth(String path) {
        return SKIP_AUTH_PATHS.stream().anyMatch(skipPath -> 
            path.startsWith(skipPath) || path.contains(skipPath)
        );
    }

    /**
     * 处理认证错误
     */
    private Mono<Void> handleAuthenticationError(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        String errorResponse = String.format(
            "{\"code\": 401, \"message\": \"%s\", \"data\": null, \"timestamp\": %d}",
            message,
            System.currentTimeMillis()
        );

        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
        // 配置类，目前为空
    }
}