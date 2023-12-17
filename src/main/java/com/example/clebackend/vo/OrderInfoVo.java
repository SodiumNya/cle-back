package com.example.clebackend.vo;

import com.example.clebackend.dto.GoodDto;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class OrderInfoVo {

    private String orderSn;

    private String createTime;

    private Integer statue;

    private Double payPrice;

    private String shopName;
    private String shopLogo;

    private List<GoodDto> goodList;

}
