package com.sfs.service;

import com.sfs.model.dto.LoginQuery;
import com.sfs.model.entity.UserAccount;
import com.sfs.utils.result.Result;

/**
 * 认证服务接口 - 用于dubbo服务暴露
 * 放在common-service中，避免服务间直接依赖
 */
public interface AuthService {

    /**
     * 用户登出
     * @param userId 用户ID
     * @return 操作结果
     */
    Result out(int userId);

    /**
     * 根据token获取用户
     * @param token 用户token
     * @return 用户信息
     */
    UserAccount getUserByToken(String token);

    /**
     * 微信登录
     * @param qtParam 登录参数
     * @return 登录结果
     */
    String wxLogin(String qtParam);

    /**
     * 用户登录
     * @param loginQuery 登录参数
     * @return 登录结果
     */
    Result login(LoginQuery loginQuery);

}