package com.example.clebackend.entity.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.lang.reflect.Type;

/**
 * @author wangqi
 * 商品属性键表
 */
@Data
@TableName("attribute_key")
public class AttributeKey {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 大类id
     */
    @TableField("category_id")
    private Integer categoryID;

    /**
     * 键名
     */
    @TableField("name")
    private String name;
}
