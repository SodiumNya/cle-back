package com.example.clebackend.request;

import com.example.clebackend.dto.GoodDto;
import lombok.Data;

import java.util.List;


@Data
public class OrderItemRequest {
    private Integer shopId;

    private String shopName;

    private Double payPrice;

    private String reciverName;

    private String reciverPhone;

    private Integer payType;

    private String note;

    private List<GoodDto> goodList;

    // Getter and Setter methods

}
