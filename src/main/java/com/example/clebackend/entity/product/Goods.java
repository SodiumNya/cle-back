package com.example.clebackend.entity.product;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author wangqi
 * 商品表
 */
@Data
@TableName("goods")
public class Goods {
    /**
     * 主键 id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 大类id
     */
    @TableField("category_id")
    private Integer categoryId;

    /**
     * 商品名字
     */
    @TableField("name")
    private String name;


    /**
     * 商品主图
     */
    @TableField(value = "main_image")
    private String mainImage;

    /**
     * 商品详情
     */
    private String detail;

    /**
     * 商店id 外键
     */
    private Integer shopId;

    /**
     * 综合得分
     */
    private Double score;

    /**
     * 总得分
     */
    private Double sumScore;

    /**
     * 销量
     */
    private Long sale;

    /**
     * 参数列表
     */
    private String attributeList;

    /**
     * 开始时间
     */
    private Timestamp startTime;

    /**
     * 结束时间
     */
    private Timestamp endTime;

    /**
     * 是否整天售卖
     */
    private Integer allDaySale;

    /**
     * 售卖状态
     */
    private Integer saleState;

    /**
     * 逻辑删除
     */
    private Integer deleteStatus;

}
