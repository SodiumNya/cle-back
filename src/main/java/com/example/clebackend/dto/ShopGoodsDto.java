package com.example.clebackend.dto;


import lombok.Data;

@Data
public class ShopGoodsDto {
    private Integer id;

    private String name;

    private Integer allDaySale;

    private Integer saleState;

    private String mainImage;

    private Double minPrice;

    private Double maxPrice;

    private Integer sale;

    private Double score;


}
