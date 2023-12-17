package com.example.clebackend.service.cart.impl;

import com.example.clebackend.entity.cart.CartItem;
import com.example.clebackend.mapper.cart.CartItemMapper;
import com.example.clebackend.service.cart.CartItemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class CartItemServiceImpl implements CartItemService {

    @Resource
    CartItemMapper cartItemMapper;

    @Override
    public Integer insert(CartItem cartItem) {
        return cartItemMapper.insert(cartItem);
    }

    @Override
    public Integer removeById(Integer cartItemId) {
        return cartItemMapper.deleteById(cartItemId);
    }
}
