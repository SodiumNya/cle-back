package com.example.clebackend.entity.product;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.lang.reflect.Type;
import java.sql.Timestamp;

/**
 * @author wangqi
 * 商品评论表
 */
@Data
@TableName("comments")
public class Comments {

    /**
     * 主键 自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 外键 用户id
     */
    private Integer userId;

    /**
     * 商品id 外键
     */
    private Integer goodsId;

    /**
     * 内容
     */
    private String content;

    /**
     * 评分
     */
    private Double score;

    /**
     * 创建时间
     */
    private Timestamp created;

    /**
     * 删除时间
     */
    private Timestamp deleteTime;

    /**
     * 修改时间
     */
    private Timestamp modifyTIme;

    /**
     * 删除时间
     */
    private int deleteState;
}
