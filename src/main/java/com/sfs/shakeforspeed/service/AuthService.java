package com.sfs.shakeforspeed.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    /**
     * 验证用户身份
     * @param username 用户名
     * @param password 密码
     * @return 用户角色(admin/merchant)或null(验证失败)
     */
    public String validateUser(String username, String password) {
        // 实际项目中应该查询数据库验证用户
        // 这里暂时使用硬编码验证，等数据库表结构完善后替换为数据库验证
        
        // 管理员账号
        if ("admin".equals(username) && "123456".equals(password)) {
            return "admin";
        } 
        // 商户账号
        else if ("zhangsan".equals(username) && "123456".equals(password)) {
            return "merchant";
        }
        
        // 验证失败
        return null;
    }
}