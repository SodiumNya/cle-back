package com.example.clebackend.util;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.clebackend.entity.admin.Admin;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.service.admin.AdminService;
import com.example.clebackend.service.user.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Objects;

@Component
public class TokenUtils {

    private static UserService staticUserService;
    private static AdminService staticAdminService;

    @Resource
    private UserService userService;

    @Resource
    private AdminService adminService;

    @PostConstruct
    public void setUserService(){
        staticUserService = userService;
    }

    @PostConstruct
    public void setAdminService(){
        staticAdminService = adminService;
    }

    public static String generateToken(String uid, String sign){
        return JWT.create()
                .withAudience(uid)
                .withExpiresAt(DateUtil.offsetDay(new Date(), 3))
                .sign(Algorithm.HMAC256(sign));

    }

    public static User getCurrentUser(){
        try{
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest() ;
            String token = request.getHeader("token");
            if(StrUtil.isBlank(token)){
                return null;
            }
            String UserId = JWT.decode(token).getAudience().get(0);

            return staticUserService.getUserByAccount(UserId);
        }catch (Exception e){
            return null;
        }

    }

    public static Admin getCurrentAdmin(){
        try{
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest() ;
            String token = request.getHeader("token");
            if(StrUtil.isBlank(token)){
                return null;
            }
            String UserId = JWT.decode(token).getAudience().get(0);

            return staticAdminService.getAdminByAccount(UserId);
        }catch (Exception e){
            return null;
        }

    }
}
