package com.example.clebackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartItemDto {

    private Integer shopId;

    private String shopName;

    private List<CartGoodDto> goodList;
}
