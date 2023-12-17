package com.example.clebackend.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class OrderResponse {
    private List<PayOrderItem> order;

    private double payPrice;

}
