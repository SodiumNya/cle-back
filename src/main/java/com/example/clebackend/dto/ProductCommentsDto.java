package com.example.clebackend.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Data
public class ProductCommentsDto {

//    private Integer id;

//    private Integer userId;

//    private Integer goodsId;

    private String content;

    private Double score;

    private Timestamp created;

    @TableField(exist = false)
    private String createdLocal;

    private String nickname;

    private String avatar;

}
