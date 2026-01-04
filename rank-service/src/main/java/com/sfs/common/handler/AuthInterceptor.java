package com.sfs.common.handler;

import com.sfs.common.annotation.Ignored;
import com.sfs.common.util.JwtUtil;
import com.sfs.model.entity.UserAccount;
import com.sfs.model.enums.AppHttpCodeEnum;
import com.sfs.utils.context.AccountContext;
import com.sfs.utils.result.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class AuthInterceptor implements HandlerInterceptor {

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
        
        // 直接从请求头获取Token
        String token = request.getHeader("Token");
        System.out.println("获取到的Token: " + token);
        
        if (token == null || token.isEmpty()) {
            System.out.println("未获取到Token，请求需要认证");
            returnJson(response, Result.failResult(AppHttpCodeEnum.TOKEN_REQUIRE).toString());
            return false;
        }

        // 验证Token并解析用户信息
        UserAccount userAccount = JwtUtil.jwtToObject(token, UserAccount.class);
        if (userAccount == null) {
            System.out.println("Token解析失败，无效的Token");
            returnJson(response, Result.failResult(AppHttpCodeEnum.TOKEN_INVALID).toString());
            return false;
        }

        // 设置用户上下文
        AccountContext.setAccount(userAccount);
        // 暂时使用username作为昵称
        userAccount.setNickname(userAccount.getUsername());
        System.out.println("用户上下文已设置: " + userAccount.getUsername());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("AuthInterceptor afterCompletion executed for URL: " + request.getRequestURI());
        // 清除线程本地变量，防止内存泄漏
        AccountContext.clear();
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