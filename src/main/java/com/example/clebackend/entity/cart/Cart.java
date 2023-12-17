package com.example.clebackend.entity.cart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("cart")
public class Cart {

    /**
     * 主键 自增id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 外键 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 创建时间
     */
    @TableField("created")
    private Timestamp created;

    /**
     * 更新时间
     */
    @TableField("update")
    private Timestamp update;
}
