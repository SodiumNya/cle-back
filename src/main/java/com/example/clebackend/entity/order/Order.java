package com.example.clebackend.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    /**
     * 主键 id 自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号
     */
    @TableField("order_sn")
    private String orderSn;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Timestamp createTime;

    /**
     * 用户账号
     */
    @TableField("member_account")
    private String memberAccount;

    /**
     * 取货人姓名
     */
    @TableField("reciver_name")
    private String reciverName;

    /**
     * 取货人电话
     */
    @TableField("receiver_phone")
    private String reciverPhone;

    /**
     * 实付价格
     */
    @TableField("pay_price")
    private Double payPrice;

    /**
     * 总价
     */
    @TableField("total_price")
    private Double totalPrice;

    /**
     * 支付类型 0 支付宝 1 微信支付
     */
    @TableField("pay_type")
    private Integer payType;


    /**
     * 来源
     */
    @TableField("source_type")
    private Integer sourceType;

    /**
     * 备注
     */
    @TableField("note")
    private String note;


    /**
     * 订单状态
     */
    @TableField("statue")
    private Integer statue;

    /**
     * 是否删除（逻辑删除）
     */
    @TableField("delete_statue")
    private Integer deleteStatue;

    /**
     * 支付时间
     */
    @TableField("payment_time")
    private Timestamp paymentTime;

    /**
     * 店铺id 外键
     */
    @TableField("shop_id")
    private Integer shopId;

    /**
     * 商店名字
     */
    @TableField("shop_name")
    private String shopName;

}
