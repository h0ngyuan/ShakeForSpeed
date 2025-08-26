package com.sfs.common.handler;

import com.sfs.common.annotation.Ignored;
import com.sfs.model.entity.User;
import com.sfs.model.enums.AppHttpCodeEnum;
import com.sfs.service.AuthService;
import com.sfs.utils.context.UserContext;
import com.sfs.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("AuthInterceptor preHandle executed for URL: " + request.getRequestURI());
        
        // 检查是否是HandlerMethod
        if (!(handler instanceof HandlerMethod)) {
            System.out.println("Handler is not HandlerMethod, allowing request");
            return true;
        }
        
        // 检查是否有@Ignored注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (handlerMethod.hasMethodAnnotation(Ignored.class)) {
            System.out.println("Method has @Ignored annotation, allowing request");
            return true;
        }
        
        // 获取TOKEN
        String token = request.getHeader("token");
        System.out.println("Token from header: " + token);
        token=token.replaceFirst("^Bearer_","");
        
        if (token == null || token.isEmpty()) {
            System.out.println("Token is null or empty, returning TOKEN_REQUIRE error");
            returnJson(response, Result.failResult(AppHttpCodeEnum.TOKEN_REQUIRE).toString());
            return false;

        }

        // 验证TOKEN并获取用户信�?
        // 这里需要实现一个根据token获取用户的方�?
        User user = authService.getUserByToken(token);
        if (user == null) {
            System.out.println("User not found for token, returning TOKEN_INVALID error");
            returnJson(response, Result.failResult(AppHttpCodeEnum.TOKEN_INVALID).toString());
            return false;
        }

        // 设置用户上下�?
        UserContext.setUser(user);
        System.out.println("User authenticated: " + user.getUsername());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("AuthInterceptor afterCompletion executed for URL: " + request.getRequestURI());
        // 清除线程本地变量，防止内存泄�?
        UserContext.clear();
        System.out.println("User context cleared");
    }

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(json);
        }
    }
}
