package com.sfs.websocket;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = null;
        if (request instanceof ServletServerHttpRequest servletRequest) {
            token = servletRequest.getServletRequest().getParameter("token");
            String activityId = servletRequest.getServletRequest().getParameter("activityId");
            if (activityId != null) {
                attributes.put("activityId", activityId);
            }
        }
        if (token != null && StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) > 0) {
            try {
                Object loginId = StpUtil.getLoginIdByToken(token);
                attributes.put("userId", String.valueOf(loginId));
                return true;
            } catch (Exception e) {
                log.warn("WebSocket auth failed: invalid token");
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
