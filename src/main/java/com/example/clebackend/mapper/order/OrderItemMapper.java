package com.example.clebackend.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clebackend.dto.GoodDto;
import com.example.clebackend.entity.order.Order;
import com.example.clebackend.entity.order.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
