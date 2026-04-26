package com.sfs.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.sfs.dto.WxLoginDTO;
import com.sfs.service.AuthService;
import com.sfs.util.Result;
import com.sfs.vo.TokenVO;
import com.sfs.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/wx-login")
    public Result<TokenVO> wxLogin(@Valid @RequestBody WxLoginDTO dto) {
        return Result.success(authService.wxLogin(dto));
    }

    @PostMapping("/phone-login")
    public Result<TokenVO> phoneLogin(@RequestParam String phone, @RequestParam String code) {
        return Result.success(authService.phoneLogin(phone, code));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/current")
    public Result<UserVO> currentUser() {
        return Result.success(authService.getCurrentUser());
    }
}
