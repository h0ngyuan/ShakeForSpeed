package com.sfs.common.handler;

import com.sfs.common.util.JwtUtil;
import com.sfs.model.entity.UserAccount;
import com.sfs.utils.context.AccountContext;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * WebSocket认证握手拦截器
 * 用于验证WebSocket连接中的Token并提取用户信息
 */
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从HTTP请求中获取Token
        String token = null;
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            token = servletRequest.getHeader("Token");
            // 尝试从查询参数中获取Token（SockJS可能会使用查询参数传递Token）
            if (token == null) {
                token = servletRequest.getParameter("token");
            }
        }

        System.out.println("WebSocket连接尝试，获取到Token: " + token);

        if (token == null || token.isEmpty()) {
            System.out.println("WebSocket连接缺少Token，拒绝连接");
            return false;
        }

        // 验证Token并解析用户信息
        UserAccount userAccount = JwtUtil.jwtToObject(token, UserAccount.class);
        if (userAccount == null) {
            System.out.println("WebSocket连接Token无效，拒绝连接");
            return false;
        }

        // 将用户信息存储到会话属性中
        attributes.put("userAccount", userAccount);
        // 设置到当前线程上下文，方便后续处理
        AccountContext.setAccount(userAccount);
        
        System.out.println("WebSocket连接认证成功，用户: " + userAccount.getUsername());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // 握手完成后清除线程上下文
        AccountContext.clear();
        System.out.println("WebSocket握手完成，清除线程上下文");
    }
}