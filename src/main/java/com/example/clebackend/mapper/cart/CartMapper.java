package com.example.clebackend.mapper.cart;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clebackend.dto.CartDto;
import com.example.clebackend.entity.cart.Cart;
import com.example.clebackend.entity.cart.CartItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

//    @Select("SELECT DISTINCT " +
//            "cart.id AS cart_id, " +
//            "shop.id AS shop_id, " +
//            "shop.name AS shop_name, " +
//            "goods.id AS good_id, " +
//            "goods.name AS good_name, " +
//            "goods_specs.id AS specs_id, " +
//            "goods_specs.specs " +
//            "FROM " +
//            "user " +
//            "LEFT JOIN cart ON user.id = cart.user_id " +
//            "LEFT JOIN cart_item ON cart.id = cart_item.cart_id " +
//            "LEFT JOIN goods ON cart_item.good_id = goods.id " +
//            "LEFT JOIN goods_specs ON goods_specs.id = cart_item.specs_id " +
//            "LEFT JOIN shop ON shop.id = goods.shop_id " +
//            "where user.id = #{userId}")
    CartDto getCartByUserId(@Param("userId") Integer userId);

    List<Integer> getCartItemId(@Param("userId")Integer userId, @Param("goodId")Integer goodId, @Param("specsId")Integer specsId);
}
