package com.example.clebackend.dto;


import lombok.Data;

import java.util.Map;

public class CartGoodDto extends GoodDto{
    private Integer cartItemId;

    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }
}
