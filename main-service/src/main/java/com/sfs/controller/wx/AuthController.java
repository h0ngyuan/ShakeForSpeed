package com.sfs.controller.wx;

import com.sfs.service.AuthService;
import com.sfs.utils.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("wx/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @ApiOperation("微信登陆")
    @PostMapping("/login")
    public Result login(@RequestParam("QtParam") String qtParam){
        return authService.wxLogin(qtParam);
    }

    @ApiOperation("微信登出")
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request){
        return authService.wxLogout(request);
    }
}
