package com.example.clebackend.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("invitation_code")
public class InvitationCodeVo {

    private Integer id;

    @TableField("code")
    private String code;

    @TableField("expiration_date")
    private String expirationDate;

    @TableField("active_status")
    private Integer activeStatus;

    @TableField("create_time")
    private Timestamp createTime;

}
