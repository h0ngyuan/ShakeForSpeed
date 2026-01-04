package com.sfs.controller;

import com.sfs.common.annotation.Ignored;
import com.sfs.model.dto.LoginQuery;
import com.sfs.service.AuthService;
import com.sfs.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @Ignored
    @PostMapping("/login")
    public Result login(@RequestBody LoginQuery loginQuery) {
        return authService.login(loginQuery);
    }

    @PostMapping("/logout")
    public Result logout(@RequestParam("id") int userId, HttpServletRequest request) {
        return authService.logout(userId,request);
    }

}