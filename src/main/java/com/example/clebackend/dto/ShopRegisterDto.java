package com.example.clebackend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ShopRegisterDto {

    @NotEmpty(message = "账户不能为空")
    @NotBlank(message = "账户不能为空")
    private String account;

    @NotEmpty(message = "账户不能为空")
    @NotBlank(message = "账户不能为空")
    private String password;

    @NotEmpty(message = "两次密码不一致")
    @NotBlank(message = "两次密码不一致")
    private String verifyPassword;

    @NotEmpty(message = "没有邀请码不能注册")
    @NotBlank(message = "没有邀请码不能注册")
    private String invitationCode;
}
