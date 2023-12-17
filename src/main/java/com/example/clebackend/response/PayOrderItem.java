package com.example.clebackend.response;

import com.example.clebackend.dto.GoodDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Data
public class PayOrderItem {
    private int shopId;
    private String shopName;
    private Double payPrice;
    private List<GoodDto> goodList;



    // Getter and Setter methods

}
