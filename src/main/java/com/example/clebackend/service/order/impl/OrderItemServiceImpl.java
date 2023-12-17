package com.example.clebackend.service.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.clebackend.entity.order.OrderItem;
import com.example.clebackend.mapper.order.OrderItemMapper;
import com.example.clebackend.service.order.OrderItemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Resource
    OrderItemMapper orderItemMapper;


    @Override
    public List<OrderItem> getGoodIdByAccountAndOrderSn(String orderSn) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();

        queryWrapper.select("good_id");
        queryWrapper.eq("order_sn", orderSn);
        return orderItemMapper.selectList(queryWrapper);
    }


    @Override
    public Integer updateReviewStatus(String orderSn, Integer goodsId, Integer status) {
        UpdateWrapper<OrderItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_sn", orderSn)
                .eq("good_id", goodsId);
        updateWrapper.set("review_status", status);
        return orderItemMapper.update(updateWrapper);
    }
}
