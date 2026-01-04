package com.sfs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfs.common.util.JwtUtil;
import com.sfs.mapper.AuthMapper;
import com.sfs.model.dto.LoginQuery;
import com.sfs.model.entity.UserAccount;
import com.sfs.service.AuthService;
import com.sfs.utils.result.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@DubboService( interfaceClass =AuthService.class)
@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, UserAccount> implements com.sfs.service.AuthService {

    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AuthService proxy;

    @Override
    public Result wxLogin(String qtParam) {
        UserAccount qw = new UserAccount();
        qw.setUid(qtParam);
        List<UserAccount> accounts = authMapper.selectList(new LambdaQueryWrapper<>(qw));
        UserAccount account;
        if (accounts==null||accounts.isEmpty()) {
            //TODO 执行新增逻辑,这边有默认图片的
            account=new UserAccount();
            account.setUid(qtParam);
            account.setRole("用户");
            account.setNickname("默认用户"+ LocalDateTime.now());
            account.setLoginCount(0);
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            account.setStatus(1);
            account.setLastLoginTime(LocalDateTime.now());
            authMapper.insert(account);
        }else {
            account = accounts.get(0);
        }
        account.setLoginCount(account.getLoginCount()+1);
        account.setLastLoginTime(LocalDateTime.now());
        authMapper.update(new LambdaUpdateWrapper<>(account));
        String k = UUID.randomUUID().toString().replace("-", "");
        String v = JwtUtil.objectToJwt(account);
        stringRedisTemplate.opsForValue().set(k,v,2,TimeUnit.HOURS);
        return Result.successResult(k);
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public Result login(LoginQuery loginQuery) {
        if(loginQuery.getAccount()==null||loginQuery.getPassword()==null)
            return Result.failResult(400,"账号密码不完整");
        UserAccount account = new UserAccount();
        account.setUid(loginQuery.getAccount());
        List<UserAccount> userAccounts =
                authMapper.selectList(new LambdaQueryWrapper<>(account));
        if (userAccounts==null||userAccounts.isEmpty()){
            return Result.failResult(500,"查无此账号");
        }
        UserAccount user = userAccounts.stream()
                .filter(a -> a.getPassword()
                        .equals(loginQuery.getPassword()))
                .toList().get(0);
        if (user.getStatus()==0){
            return Result.failResult(400,"账户已注销");
        }
        if (user.getBanUtil()!=null&&user.getBanUtil().isAfter(LocalDateTime.now())){
            return Result.failResult(400,"账号封禁中，还需要等待"+ Duration.between(user.getBanUtil(),LocalDateTime.now()) +"秒");
        }
        user.setLoginCount(user.getLoginCount()+1);
        authMapper.update(new LambdaUpdateWrapper<>(user));
        String k = UUID.randomUUID().toString().replace("-","");
        String v = JwtUtil.objectToJwt(user);
        stringRedisTemplate.opsForValue().set(k,v,12, TimeUnit.HOURS);
        return Result.successResult(k);
    }

    @Override
    public Result logout(int userId, HttpServletRequest request) {
        stringRedisTemplate.delete(request.getHeader("Token"));
        return Result.successResult();
    }

    @Override
    public Result wxLogout(HttpServletRequest request) {
        stringRedisTemplate.delete(request.getHeader("Token"));
        return Result.successResult();
    }


}
