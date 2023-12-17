package com.example.clebackend.service.order;

import com.example.clebackend.entity.order.Order;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.request.OrderItemRequest;
import com.example.clebackend.vo.OrderDetailVo;
import com.example.clebackend.vo.OrderInfoVo;

import java.util.List;

public interface OrderService {

    Integer generateOrder(List<OrderItemRequest> reqOrder, User user);

    List<OrderInfoVo> getInfoByUserAccount(String account, Integer status, Integer asc);
    OrderDetailVo getDetail(String orderSn);

    Integer updateOrderStatue(String orderSn, Integer statue);

    Order getOrderStatus(String orderSn);

    List<OrderInfoVo> getInfoByShopId(Integer shopId, Integer statue, Integer asc);



}
