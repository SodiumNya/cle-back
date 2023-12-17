package com.example.clebackend.entity.user;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class User {
    /**
     * 主键 id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 用户密码
     */
    private String password;

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
    @TableField(exist = false)
    private String token;

}
