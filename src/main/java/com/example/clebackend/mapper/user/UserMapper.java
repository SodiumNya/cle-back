package com.example.clebackend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clebackend.entity.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    int updateByPrimaryKeySelective(User record);
}
