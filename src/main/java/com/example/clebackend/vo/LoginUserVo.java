package com.example.clebackend.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class LoginUserVo {

    /**
     * 主键 id
     */
    private Integer id;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户头像 url
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 用户状态
     */
    private int state;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 登陆成功后签发的jwt
     */
    private String token;
}
