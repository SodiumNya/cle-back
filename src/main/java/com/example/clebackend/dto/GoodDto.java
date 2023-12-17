package com.example.clebackend.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class GoodDto {
    private Integer goodId;

    private String goodName;

    private Integer specsId;

    private String specs;

    private Map<String, String> goodSpecsJSon;

    private Integer quantity;

    private Double price;

    private Double totalPrice;

    private String mainImage;

    private Integer reviewStatus;

}
