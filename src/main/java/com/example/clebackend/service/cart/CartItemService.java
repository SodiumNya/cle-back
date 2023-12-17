package com.example.clebackend.service.cart;

import com.example.clebackend.entity.cart.CartItem;

public interface CartItemService {
    Integer insert(CartItem cartItem);

    Integer removeById(Integer cartItemId);
}
