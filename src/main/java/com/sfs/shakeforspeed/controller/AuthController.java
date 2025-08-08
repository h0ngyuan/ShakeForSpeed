package com.sfs.shakeforspeed.controller;

import com.sfs.shakeforspeed.entity.User;
import com.sfs.shakeforspeed.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        String username = user.getUsername();
        String password = user.getPassword();
        
        // 验证用户
        String role = authService.validateUser(username, password);
        
        if (role != null) {
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("role", role);
            response.put("username", username);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }
        
        return response;
    }
}