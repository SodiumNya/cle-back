package com.example.clebackend.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.entity.admin.InvitationCode;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.exception.ServiceException;
import com.example.clebackend.mapper.user.UserMapper;
import com.example.clebackend.service.admin.AdminService;
import com.example.clebackend.service.user.ShopService;
import com.example.clebackend.service.user.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;

    @Resource
    ShopService shopService;

    @Resource
    AdminService adminService;

    @Override
    public User getUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("account", user.getAccount())
                .eq("password", user.getPassword());
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserByAccount(String account) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public void addUser(User user) {
        userMapper.insert(user);

    }

    @Override
    public void addShopUser(User user, String invitationCode) {
        userMapper.insert(user);

        InvitationCode data = new InvitationCode();
        data.setCode(invitationCode);
        InvitationCode resData = adminService.getInvitationCodeByCode(data);
        if(resData == null || resData.getActiveStatus() == 1)
            throw new ServiceException(ResponseCode.FAIL.getCode(), "激活码已过期");

        int res = shopService.addShop(user.getAccount(), invitationCode);
        if(res <= 0) TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }

    @Override
    public int update(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public int reSetPassword(String account, String newPassword) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account)
                .set("password", newPassword);
        return userMapper.update(updateWrapper);
    }
}


