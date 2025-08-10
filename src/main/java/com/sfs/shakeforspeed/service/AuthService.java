package com.sfs.shakeforspeed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sfs.shakeforspeed.model.dto.UserDTO;
import com.sfs.shakeforspeed.model.entity.User;
import com.sfs.shakeforspeed.model.vo.UserVO;
import com.sfs.shakeforspeed.utils.result.Result;
import org.springframework.stereotype.Service;

public interface AuthService extends IService<User> {
    
    /**
     * 验证用户身份
     * @param userDTO
     * @return Result
     */
    Result in(UserDTO userDTO);

    Result out(int userId);
}