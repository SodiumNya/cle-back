package com.example.clebackend.service.cart;

import com.example.clebackend.dto.CartDto;
import com.example.clebackend.entity.cart.Cart;

import java.util.List;

public interface CartService {

    CartDto getCartByUserId(Integer userId);
    Cart getCartIdByUserId(Integer userId);

    Integer insert(Cart cart);

    public List<CartDto> groupByShop(List<CartDto> cartList);

    List<Integer> getCartItemId(Integer userId, Integer goodId, Integer specsId);
}
