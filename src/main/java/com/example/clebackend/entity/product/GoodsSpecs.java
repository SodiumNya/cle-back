package com.example.clebackend.entity.product;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author wangqi
 * 商品参数表
 */
@Data
@TableName("goods_specs")
public class GoodsSpecs {

    /**
     * id 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 外键 商品id
     */
    @TableField("goods_id")
    private Integer goodsId;

    /**
     * 商品参数
     */
    @TableField("specs")
    private String specs;

    /**
     * 价格
     */
    @TableField("price")
    private Double price;

    /**
     * 库存
     */
    @TableField("stock")
    private Integer stock;
}
