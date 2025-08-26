package com.sfs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfs.mapper.AuthMapper;
import com.sfs.model.dto.UserDTO;
import com.sfs.model.entity.User;
import com.sfs.model.enums.AppHttpCodeEnum;
import com.sfs.model.vo.UserVO;
import com.sfs.service.AuthService;
import com.sfs.utils.context.UserContext;
import com.sfs.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper,User> implements AuthService  {

    @Autowired
    private AuthMapper authMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expire}")
    private long jwtExpire;

    @Override
    public Result in(UserDTO userDTO) {
        if (userDTO==null){
            return Result.failResult(AppHttpCodeEnum.LOGIN_ERROR);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,userDTO.getUsername())
                    .eq(User::getPassword,userDTO.getPassword());
        User user = authMapper.selectOne(queryWrapper);
        if (user==null){
            return Result.failResult(AppHttpCodeEnum.USER_DATA_NOT_EXIST);
        }
        UserContext.setUser(user);
        UserVO userVO= BeanUtil.toBean(user,UserVO.class);
        // 生成JWT token
        String token = generateToken(user);
        userVO.setToken(token);
        return Result.successResult(userVO);
    }

    @Override
    public Result out(int userId) {
        UserContext.clear();
        return Result.successResult(AppHttpCodeEnum.LOGIN_OUT);
    }

    @Override
    public User getUserByToken(String token) {
        try {
            // 验证token
            boolean validate = JWTUtil.verify(token, jwtSecret.getBytes());
            if (!validate) {
                return null;
            }

            // 解析token
            JWT jwt = JWTUtil.parseToken(token);
            JWTPayload payload = jwt.getPayload();
            Long userId = Long.valueOf(payload.getClaim("userId").toString());

            // 查询用户
            return authMapper.selectById(userId);
        } catch (Exception e) {
            return null;
        }
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        claims.put("exp", new Date(System.currentTimeMillis() + jwtExpire));

        return JWTUtil.createToken(claims, jwtSecret.getBytes());
    }
}
