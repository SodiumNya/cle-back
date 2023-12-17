package com.example.clebackend.service.user.impl;

import cn.hutool.extra.mail.Mail;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.exception.ServiceException;
import com.example.clebackend.service.user.EmailService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String username;

    @Resource
    private JavaMailSenderImpl mailSender;
    @Override
    public void send(String email, HttpSession session) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setSubject("cle验证码");

            String code = "123456";
            session.setAttribute("email", email);
            session.setAttribute("code", code);

            mailMessage.setText("验证码："+code);

            mailMessage.setTo(email);

            mailMessage.setFrom(username);

            mailSender.send(mailMessage);

        }catch (Exception e){
            throw new ServiceException(ResponseCode.FAIL.getCode(), "邮件发送失败");
        }
    }

    @Override
    public void sendVerificationCode(Mail mail) {

    }
}
