package com.example.clebackend.controller.user;

import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.common.RestBean;
import com.example.clebackend.dto.CartDto;
import com.example.clebackend.dto.CartItemDto;
import com.example.clebackend.dto.GoodDto;
import com.example.clebackend.entity.cart.Cart;
import com.example.clebackend.entity.cart.CartItem;
import com.example.clebackend.entity.product.GoodsSpecs;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.service.cart.CartItemService;
import com.example.clebackend.service.cart.CartService;
import com.example.clebackend.service.good.ProductService;
import com.example.clebackend.util.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    ProductService productService;

    @Resource
    CartService cartService;

    @Resource
    CartItemService cartItemService;

    @PostMapping("add")
    @Transactional
    public RestBean<String> addCart(@RequestBody CartItemDto cartItemDto){
        if(cartItemDto == null) {
            return RestBean.error(ResponseCode.FAIL.getCode(), "空项目");
        }

        User currentUser = TokenUtils.getCurrentUser();
//        User currentUser = new User();
//        currentUser.setId(0);
        if(currentUser == null)
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "未登录");

        Cart cart = cartService.getCartIdByUserId(currentUser.getId());
        if(cart == null){
            cart = new Cart();
            cart.setUserId(currentUser.getId());
            Integer res = cartService.insert(cart);
            if(res < 0)
                return RestBean.error(ResponseCode.FAIL.getCode(), "添加失败");
        }


        //TODO 修改getPriceBySpec
        GoodDto goods = cartItemDto.getGoodList().get(0);
        GoodsSpecs data = productService.getSpecsBySpec(goods.getGoodId(),goods.getSpecs());

        if (data == null || data.getId() == null){
            return RestBean.error(ResponseCode.FAIL.getCode(), "添加失败");
        }

        CartItem cartItem = new CartItem();

        cartItem.setCartId(cart.getId());
        cartItem.setGoodId(goods.getGoodId());
        cartItem.setSpecsId(data.getId());
        cartItem.setCreated(new Timestamp(System.currentTimeMillis()));

        Integer res = cartItemService.insert(cartItem);
        if(res < 0)
            return RestBean.error(ResponseCode.FAIL.getCode(), "添加失败");

        return RestBean.success("添加成功");

    }

    @GetMapping("get")
    public RestBean<CartDto> getCart(){
        User user = TokenUtils.getCurrentUser();
//        User user = new User();
//        user.setId(0);
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权访问");

        CartDto cartDto = cartService.getCartByUserId(user.getId());



        return RestBean.success(cartDto);
    }

    @PostMapping("remove")
    public RestBean<String> removeItem(@RequestBody CartItem cartItem){

        Integer res = cartItemService.removeById(cartItem.getId());
        if(res <= 0)
            return RestBean.error(ResponseCode.FAIL.getCode(), "失败");

        return RestBean.success("成功");

    }
}
