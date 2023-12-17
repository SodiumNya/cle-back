package com.example.clebackend.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

public class GoodInfoDto {

    private Integer id;

    @TableField("name")
    private String name;

    @TableField("main_image")
    private String mainImage;
    private Double score;

    private Long sale;

    private Double price;
}
