package com.example.clebackend.entity.product;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author wangqi
 * 商品属性值
 *
 */
@Data
@TableName("attribute_value")
public class AttributeValue {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 键id
     */
    @TableField("attribute_key_id")
    private Integer attributeKeyId;

    /**
     * 取值
     */
    @TableField("name")
    private String name;
}
