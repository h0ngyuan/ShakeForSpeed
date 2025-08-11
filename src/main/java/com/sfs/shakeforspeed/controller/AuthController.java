package com.sfs.shakeforspeed.controller;

import com.sfs.shakeforspeed.common.annotation.Ignored;
import com.sfs.shakeforspeed.model.dto.UserDTO;
import com.sfs.shakeforspeed.service.AuthService;
import com.sfs.shakeforspeed.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @Ignored
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        return authService.in(userDTO);
    }

    @PostMapping("/out")
    public Result out(@RequestParam("id") int userId) {
        return authService.out(userId);
    }

}