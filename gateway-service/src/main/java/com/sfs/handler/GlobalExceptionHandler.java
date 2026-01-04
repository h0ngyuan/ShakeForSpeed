package com.sfs.handler;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 网关全局异常处理器
 * 统一处理网关层发生的各种异常
 */
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        // 设置响应头
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        // 根据异常类型设置不同的响应
        String errorMessage;
        HttpStatus httpStatus;
        
        if (ex instanceof NotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
            errorMessage = "Service not found";
        } else if (ex instanceof ResponseStatusException responseStatusException) {
            httpStatus = responseStatusException.getStatus();
            errorMessage = responseStatusException.getReason();
        } else if (ex instanceof java.net.ConnectException) {
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            errorMessage = "Service temporarily unavailable";
        } else if (ex instanceof java.util.concurrent.TimeoutException) {
            httpStatus = HttpStatus.GATEWAY_TIMEOUT;
            errorMessage = "Service response timeout";
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = "Internal server error";
        }
        
        response.setStatusCode(httpStatus);
        
        // 构建错误响应
        String errorResponse = buildErrorResponse(
            httpStatus.value(),
            errorMessage,
            exchange.getRequest().getURI().getPath()
        );
        
        DataBuffer buffer = response.bufferFactory().wrap(
            errorResponse.getBytes(StandardCharsets.UTF_8)
        );
        
        return response.writeWith(Mono.just(buffer));
    }
    
    /**
     * 构建错误响应JSON
     */
    private String buildErrorResponse(int code, String message, String path) {
        return String.format(
                """
                        {
                          "code": %d,
                          "message": "%s",
                          "data": null,
                          "path": "%s",
                          "timestamp": "%s"
                        }""",
            code,
            message,
            path,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}