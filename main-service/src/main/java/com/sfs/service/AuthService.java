package com.sfs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sfs.model.dto.UserDTO;
import com.sfs.model.entity.User;
import com.sfs.utils.result.Result;

public interface AuthService extends IService<User> {
    
    /**
     * 验证用户身份
     * @param userDTO
     * @return Result
     */
    Result in(UserDTO userDTO);

    Result out(int userId);

    /**
     * 根据token获取用户
     * @param token
     * @return User
     */
    User getUserByToken(String token);
}
