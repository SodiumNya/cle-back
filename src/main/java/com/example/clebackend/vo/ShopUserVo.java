package com.example.clebackend.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ShopUserVo {
    /**
     * 主键 id
     */
    private Integer id;

    private Integer shopId;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 用户头像 url
     */
    private String avatar;

    /**
     * 登陆成功后签发的jwt
     */
    @TableField(exist = false)
    private String token;

    private String role;

    private String invitation_code;

    private String expirationDate;

}
