package com.example.clebackend.service.user;

import cn.hutool.extra.mail.Mail;
import jakarta.servlet.http.HttpSession;

public interface EmailService {

    public void send(String email, HttpSession session);

    public void sendVerificationCode(Mail mail);
}
