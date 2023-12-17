package com.example.clebackend.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("oms_order_item")
public class OrderItem {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 订单id 外键
     */
    @TableField("order_id")
    private Integer orderId;

    /**
     * 订单编号
     */
    @TableField("order_sn")
    private String orderSn;

    /**
     * 商品id
     */
    @TableField("good_id")
    private Integer goodId;

    /**
     * 商品头图
     */
    @TableField("good_main_image")
    private String goodMainImage;

    /**
     * 商品名字
     */
    @TableField("good_name")
    private String goodName;

    /**
     * 商品价格
     */
    @TableField("good_price")
    private Double goodPrice;

    /**
     * 购买数量
     */
    @TableField("good_quantity")
    private Integer goodQuantity;

    /**
     * 参数id
     */
    @TableField("specs_id")
    private Integer specsId;

    /**
     * 商品参数
     */
    @TableField("good_specs")
    private String goodSpecs;

    /**
     * 大类id
     */
    @TableField("category_id")
    private Integer categoryId;
}
