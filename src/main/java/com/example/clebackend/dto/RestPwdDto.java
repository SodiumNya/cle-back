package com.example.clebackend.dto;


import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RestPwdDto {

    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    private String code;
}
