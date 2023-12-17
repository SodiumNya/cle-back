package com.example.clebackend.entity.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("goods_picture")
public class GoodsPicture {

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
     * 商品url
     */
    @TableField("url")
    private String url;

    /**
     * 创建时间
     */
    @TableField("created")
    private Timestamp created;

    /**
     * 是否删除
     */
    @TableField("delete_state")
    private Integer deleteState;
}
