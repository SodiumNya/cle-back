package com.example.clebackend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;

public class EncryptUtils {


    @Value("${salt.user}")
    private static String userSalt;

    @Value("${salt.shop}")
    private static String shopSalt;

    @Value("${salt.admin}")
    private static String adminSalt;



    public static String userPwdEncrypt(String password){
        return DigestUtils.md5DigestAsHex((password + userSalt).getBytes());
    }

    public static String shopPwdEncrypt(String password){
        return DigestUtils.md5DigestAsHex((password + shopSalt).getBytes());
    }

    public static String adminPwdEncrypt(String password){
        return DigestUtils.md5DigestAsHex((password + adminSalt).getBytes());
    }
}
