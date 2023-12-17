package com.example.clebackend.service.cart.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.clebackend.dto.CartDto;
import com.example.clebackend.dto.CartItemDto;
import com.example.clebackend.entity.cart.Cart;
import com.example.clebackend.mapper.cart.CartMapper;
import com.example.clebackend.service.cart.CartService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    CartMapper cartMapper;

    @Override
    public CartDto getCartByUserId(Integer userId) {

        return cartMapper.getCartByUserId(userId);
    }

    @Override
    public Cart getCartIdByUserId(Integer userId) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.select("id");

        return cartMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer insert(Cart cart) {
        return cartMapper.insert(cart);
    }


    @Override
    public List<Integer> getCartItemId(Integer userId, Integer goodId, Integer specsId) {
        return cartMapper.getCartItemId(userId, goodId, specsId);
    }


    @Override
    public List<CartDto> groupByShop(List<CartDto> cartList) {
        return cartList.stream()
                .map(cartDto -> {
                    List<CartItemDto> groupedCartItems = cartDto.getCartItemList().stream()
                            .collect(Collectors.groupingBy(CartItemDto::getShopId))
                            .entrySet().stream()
                            .map(entry -> {
                                CartItemDto groupedCartItem = new CartItemDto();
                                groupedCartItem.setShopId(entry.getKey());
                                groupedCartItem.setShopName(entry.getValue().get(0).getShopName());
                                groupedCartItem.setGoodList(entry.getValue().stream()
                                        .flatMap(cartItemDto -> cartItemDto.getGoodList().stream())
                                        .collect(Collectors.toList()));
                                return groupedCartItem;
                            })
                            .collect(Collectors.toList());

                    CartDto groupedCartDto = new CartDto();
                    groupedCartDto.setId(cartDto.getId());
                    groupedCartDto.setUserId(cartDto.getUserId());
                    groupedCartDto.setCartItemList(groupedCartItems);

                    return groupedCartDto;
                })
                .collect(Collectors.toList());
    }

}
