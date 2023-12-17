package com.example.clebackend.entity.admin;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("invitation_code")
public class InvitationCode {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("code")
    private String code;

    @TableField("expiration_date")
    private Timestamp expirationDate;

    @TableField("active_status")
    private Integer activeStatus;

    @TableField("create_time")
    private Timestamp createTime;

    @TableField("update_time")
    private Timestamp updateTime;
}
