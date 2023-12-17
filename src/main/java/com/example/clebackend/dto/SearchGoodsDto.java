package com.example.clebackend.dto;

import lombok.Data;

import java.util.List;


@Data
public class SearchGoodsDto {
    private String canteenName;

    private List<ShopGoodsDto> goodsList;
}
