package com.sfs.shakeforspeed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sfs.shakeforspeed.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper extends BaseMapper<User> {

}
