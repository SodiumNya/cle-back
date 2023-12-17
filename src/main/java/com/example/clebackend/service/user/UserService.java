package com.example.clebackend.service.user;

import com.example.clebackend.entity.user.User;
import org.apache.ibatis.annotations.Param;


public interface UserService {
//    User getRole(User user);

    User getUser(User user);
    User getUserByAccount(String account);

    void addUser(User user);
    void addShopUser(User user, String invitationCode);

    int update(User user);

    int reSetPassword(@Param("account") String account, @Param("newPassword") String newPassword);

//    void deleteUser(User user);
}
