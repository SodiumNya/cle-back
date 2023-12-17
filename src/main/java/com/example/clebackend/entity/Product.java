package com.example.clebackend.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("product")
public class Product {

    /**
     * 产品id
     */
    private Integer productId;

    /**
     * 名字
     */
    private String name;

    /**
     * 所属位置
     */
    private String location;

    private Double price;

    private Float score;

    private Integer sale;

    @TableField(exist = false)
    private List<String> urlList;

}
