package com.sfs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sfs.model.dto.LoginQuery;
import com.sfs.model.entity.UserAccount;
import com.sfs.utils.result.Result;

import javax.servlet.http.HttpServletRequest;

public interface AuthService extends IService<UserAccount> {

    Result wxLogin(String qtParam);

    Result login(LoginQuery loginQuery);

    Result logout(int userId, HttpServletRequest request);

    Result wxLogout(HttpServletRequest request);
}
