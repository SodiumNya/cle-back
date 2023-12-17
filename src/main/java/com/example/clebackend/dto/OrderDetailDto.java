package com.example.clebackend.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class OrderDetailDto {

    private String orderSn;

    private Timestamp dateCreateTime;

    private Integer statue;

    private Double payPrice;

    private String shopName;
    private String shopLogo;

    private List<GoodDto> goodList;

    private String note;

}
