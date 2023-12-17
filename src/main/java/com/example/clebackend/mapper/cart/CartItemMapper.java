package com.example.clebackend.mapper.cart;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clebackend.entity.cart.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
