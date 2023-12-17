package com.example.clebackend.service.order;

import com.example.clebackend.entity.order.OrderItem;

import java.util.List;


public interface OrderItemService {
    List<OrderItem> getGoodIdByAccountAndOrderSn(String orderSn);


    Integer updateReviewStatus(String orderSn, Integer goodsId, Integer status);

}
