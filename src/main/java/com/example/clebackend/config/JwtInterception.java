package com.example.clebackend.config;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.exception.ServiceException;
import com.example.clebackend.service.user.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author wangqi
 * 同意验证token
 */
public class JwtInterception implements HandlerInterceptor {

    @Resource
    UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断是否有token
        String token = request.getParameter("token");
        if(StrUtil.isBlank(token)) token = request.getHeader("token");

        if(StrUtil.isBlank(token)) throw new ServiceException(ResponseCode.FAIL.getCode(), "身份验证未通过");

        String account;
        User user;

        try {
            account = JWT.decode(token).getAudience().get(0);

            user = userService.getUserByAccount(account);
        }catch (Exception e){
            String msg = "token验证失败, 请重试";
            throw new ServiceException(ResponseCode.FAIL.getCode(), msg);
        }

        //判断用户信息
        if (user == null) throw new ServiceException(ResponseCode.FAIL.getCode(), "用户不存在");

        //验证token
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();

            jwtVerifier.verify(token);
        }catch (JWTVerificationException e){
            String msg = "token验证失败, 请重试";
            throw new ServiceException(ResponseCode.FAIL.getCode(), msg);
        }

        return true;
    }
}
