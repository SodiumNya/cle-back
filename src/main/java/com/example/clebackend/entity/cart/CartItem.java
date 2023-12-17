package com.example.clebackend.entity.cart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

@Data
@TableName("cart_item")
public class CartItem {

    /**
     * 主键, 非业务 自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 外键 购物车id
     */
    @TableField("cart_id")
    private Integer cartId;

    /**
     * 商品id
     */
    @TableField("good_id")
    private Integer goodId;

    /**
     * 参数id
     */
    @TableField("specs_id")
    private Integer specsId;

    /**
     * 创建时间
     */
    @TableField("created")
    private Timestamp created;
}
