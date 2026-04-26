package com.sfs.service;

import cn.dev33.satoken.stp.StpUtil;
import com.sfs.dto.WxLoginDTO;
import com.sfs.entity.User;
import com.sfs.enums.AppHttpCodeEnum;
import com.sfs.exception.BusinessException;
import com.sfs.mapper.UserMapper;
import com.sfs.util.SnowflakeIdGenerator;
import com.sfs.vo.TokenVO;
import com.sfs.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;

    public TokenVO wxLogin(WxLoginDTO dto) {
        String openid = mockWxAuth(dto.getCode());
        User user = findOrCreateUser(openid);
        StpUtil.login(user.getId());
        TokenVO vo = new TokenVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setExpireAt(System.currentTimeMillis() + StpUtil.getTokenTimeout() * 1000);
        return vo;
    }

    public TokenVO phoneLogin(String phone, String code) {
        if (!"123456".equals(code)) {
            throw new BusinessException(AppHttpCodeEnum.BAD_REQUEST, "验证码错误");
        }
        User user = new User();
        user.setUid(SnowflakeIdGenerator.getInstance().nextUid());
        user.setNickname("user_" + phone.substring(7));
        user.setStatus(1);
        userMapper.insert(user);
        StpUtil.login(user.getId());
        TokenVO vo = new TokenVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setExpireAt(System.currentTimeMillis() + StpUtil.getTokenTimeout() * 1000);
        return vo;
    }

    public void logout() {
        StpUtil.logout();
    }

    public UserVO getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(AppHttpCodeEnum.USER_NOT_FOUND);
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUid(user.getUid());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setGender(user.getGender());
        return vo;
    }

    private String mockWxAuth(String code) {
        return "wx_openid_" + code;
    }

    private User findOrCreateUser(String openid) {
        User user = new User();
        user.setUid(SnowflakeIdGenerator.getInstance().nextUid());
        user.setNickname("user_" + openid.substring(0, 6));
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }
}
