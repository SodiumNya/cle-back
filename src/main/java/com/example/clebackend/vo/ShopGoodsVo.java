package com.example.clebackend.vo;


import lombok.Data;

@Data
public class ShopGoodsVo {
    private Integer id;

    private String name;

    private Integer allDaySale;

    private Integer saleState;

    private String mainImage;

    private String price;

    private Integer sale;

    private Double score;


}
