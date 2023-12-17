package com.example.clebackend.dto;

import lombok.Data;

@Data
public class OrderDto {
    private Integer shopId;

    private String shopName;

    private Integer goodId;

    private String goodName;

    private Integer specsId;

    private String specs;

    private Integer quantity;

    private Integer price;

    private Double totalPrice;

    private String mainImage;
}
