package com.sfs.shakeforspeed.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfs.shakeforspeed.mapper.AuthMapper;
import com.sfs.shakeforspeed.model.dto.UserDTO;
import com.sfs.shakeforspeed.model.entity.User;
import com.sfs.shakeforspeed.model.enums.AppHttpCodeEnum;
import com.sfs.shakeforspeed.model.vo.UserVO;
import com.sfs.shakeforspeed.service.AuthService;
import com.sfs.shakeforspeed.utils.context.UserContext;
import com.sfs.shakeforspeed.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper,User> implements AuthService  {

    @Autowired
    private AuthService authService;


    @Override
    public Result in(UserDTO userDTO) {
        if (userDTO==null){
            return Result.failResult(AppHttpCodeEnum.LOGIN_ERROR);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId,userDTO.getUserId())
                    .eq(User::getPassword,userDTO.getPassword());
        User user = authService.getOne(queryWrapper);
        if (user==null){
            return Result.failResult(AppHttpCodeEnum.USER_DATA_NOT_EXIST);
        }
        UserContext.setUser(user);
        UserVO userVO= BeanUtil.toBean(user,UserVO.class);
        userVO.setToken("1");
        return Result.successResult(userVO);
    }

    @Override
    public Result out(int userId) {
        UserContext.clear();
        return Result.successResult(AppHttpCodeEnum.LOGIN_OUT);
    }
}
