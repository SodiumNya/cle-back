package com.example.clebackend.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@TableName("cart")
@Data
public class CartDto {
    private Integer id;

    private Integer userId;

    private List<CartItemDto> cartItemList;
}
