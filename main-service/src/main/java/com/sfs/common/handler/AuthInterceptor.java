package com.sfs.common.handler;

import cn.hutool.jwt.JWT;
import com.sfs.common.annotation.Ignored;
import com.sfs.common.util.JwtUtil;
import com.sfs.model.entity.UserAccount;
import com.sfs.model.enums.AppHttpCodeEnum;
import com.sfs.utils.context.AccountContext;
import com.sfs.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
        // 获取Token
        String token = request.getHeader("Token");
        System.out.println("Gateway解析的用户信息 - Token: " + token);
        //找有无token
        if(token==null||token.isEmpty()){
            System.out.println("Gateway未解析到用户信息，可能未通过JWT认证");
            returnJson(response, Result.failResult(AppHttpCodeEnum.TOKEN_REQUIRE).toString());
            return false;
        }
        //看看redis中有无token
        String jwt = stringRedisTemplate.opsForValue().get(token);
        if(jwt==null||jwt.isEmpty()){
            return false;
        }
        UserAccount account = JwtUtil.jwtToObject(jwt, UserAccount.class);
        if (account==null) return false;
        AccountContext.setAccount(account);
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