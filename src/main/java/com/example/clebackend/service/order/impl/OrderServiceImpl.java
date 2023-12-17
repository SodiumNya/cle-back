package com.example.clebackend.service.order.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.clebackend.dto.GoodDto;
import com.example.clebackend.dto.OrderDetailDto;
import com.example.clebackend.dto.OrderInfoDto;
import com.example.clebackend.entity.order.Order;
import com.example.clebackend.entity.order.OrderItem;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.exception.ServiceException;
import com.example.clebackend.mapper.order.OrderItemMapper;
import com.example.clebackend.mapper.order.OrderMapper;
import com.example.clebackend.request.OrderItemRequest;
import com.example.clebackend.service.cart.CartItemService;
import com.example.clebackend.service.cart.CartService;
import com.example.clebackend.service.good.ProductService;
import com.example.clebackend.service.order.OrderItemService;
import com.example.clebackend.service.order.OrderService;
import com.example.clebackend.util.GenOrderSnUtils;
import com.example.clebackend.util.TimeFormatUtils;
import com.example.clebackend.vo.OrderDetailVo;
import com.example.clebackend.vo.OrderInfoVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl implements OrderService {


    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderItemMapper orderItemMapper;

    @Resource
    OrderItemService orderItemService;

    @Resource
    ProductService productService;

    @Resource
    CartService cartService;

    @Resource
    CartItemService cartItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer generateOrder(List<OrderItemRequest> reqOrder, User user) {
        // 检查用户信息是否完整
        if (Objects.isNull(user) || Objects.isNull(user.getAccount()) || Objects.isNull(user.getPhone())) {
            return -1;
        }

        List<Order> orderList = new ArrayList<>();
        List<List<OrderItem>> orderItemList = reqOrder.stream()
                .map(item -> {
                    // 创建订单
                    Order order = createOrder(user, item);
                    orderList.add(order);

                    // 创建订单项列表
                    return createOrderItems(order, item);

                }).toList();


        // 逐个处理订单和订单项
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);

            // 设置订单创建时间

            order.setCreateTime(TimeFormatUtils.getTimeStamp());

            //TODO：接入真正的支付替换
            order.setPaymentTime(TimeFormatUtils.getTimeStamp());

            // 插入订单
            int res = orderMapper.insert(order);
            if (res <= 0) {
                throw new ServiceException("订单生成失败");
            }

            // 插入订单项
            List<OrderItem> orderItems = orderItemList.get(i);
            for (OrderItem item : orderItems) {
                item.setOrderId(order.getId());

                int insert = orderItemMapper.insert(item);
                if (insert <= 0) {
                    throw new ServiceException("订单生成失败");
                }

                // 移除购物车中对应的商品项
                removeCartItem(user, item);
            }
        }

        return 1;
    }

    @Override
    public List<OrderInfoVo> getInfoByUserAccount(String account, Integer status, Integer asc) {

        List<OrderInfoDto> orderList = orderMapper.getInfoByUserAccount(account, status, asc);

        return orderList.stream()
                .map(order -> {
                    OrderInfoVo orderInfoVo = new OrderInfoVo();
                    BeanUtil.copyProperties(order, orderInfoVo);
                    orderInfoVo.setCreateTime(TimeFormatUtils.coverToSecondData(order.getDateCreateTime()));
                    return orderInfoVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailVo getDetail(String orderSn) {
        OrderDetailDto detail = orderMapper.getDetail(orderSn);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        BeanUtil.copyProperties(detail, orderDetailVo);

        orderDetailVo.getGoodList()
                .forEach(item -> item.setTotalPrice(item.getQuantity() * item.getPrice()));

        return orderDetailVo;
    }


    @Override
    @Transactional
    public Integer updateOrderStatue(String orderSn, Integer statue) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_sn", orderSn);
        updateWrapper.set("statue", statue);
        int res = orderMapper.update(updateWrapper);
        if (res <= 0) return -1;
        if (statue != 4) return res;

        List<OrderItem> orderItemList = orderItemService.getGoodIdByAccountAndOrderSn(orderSn);

        assert orderItemList != null;

        res = productService.updateGoodsSaleByList(orderItemList);
        return res;

    }

    @Override
    public Order getOrderStatus(String orderSn) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_sn", orderSn);
        queryWrapper.select("statue");

        return orderMapper.selectOne(queryWrapper);
    }

    @Override
    public List<OrderInfoVo> getInfoByShopId(Integer shopId, Integer statue, Integer asc) {
        List<OrderInfoDto> orderList = orderMapper.getInfoByShopId(shopId, statue, asc);

        return orderList.stream()
                .map(order -> {
                    OrderInfoVo orderInfoVo = new OrderInfoVo();
                    BeanUtil.copyProperties(order, orderInfoVo);
                    orderInfoVo.setCreateTime(TimeFormatUtils.coverToSecondData(order.getDateCreateTime()));
                    return orderInfoVo;
                })
                .collect(Collectors.toList());

    }

    // 创建订单
    private Order createOrder(User user, OrderItemRequest item) {
//        Double payPrice =
        Order order = new Order();
        order.setOrderSn(GenOrderSnUtils.generate());
        order.setMemberAccount(user.getAccount());
        order.setReciverName(user.getAccount());
        order.setReciverPhone(user.getPhone());
        order.setPayPrice(item.getPayPrice());
        order.setTotalPrice(item.getPayPrice());
        order.setPayType(item.getPayType());
        order.setNote(item.getNote());
        order.setStatue(1);
        order.setShopId(item.getShopId());
        order.setShopName(item.getShopName());
        return order;
    }

    // 创建订单项列表
    private List<OrderItem> createOrderItems(Order order, OrderItemRequest item) {
        List<GoodDto> goodList = item.getGoodList();

        return goodList.stream()
                .map(goods -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderSn(order.getOrderSn());
                    orderItem.setGoodId(goods.getGoodId());
                    orderItem.setGoodMainImage(goods.getMainImage());
                    orderItem.setGoodName(goods.getGoodName());
                    orderItem.setGoodPrice(goods.getPrice());
                    orderItem.setGoodQuantity(goods.getQuantity());
                    orderItem.setGoodSpecs(goods.getSpecs());
                    orderItem.setSpecsId(goods.getSpecsId());
                    return orderItem;
                }).collect(Collectors.toList());
    }

    // 移除购物车中对应的商品项
    private void removeCartItem(User user, OrderItem item) {
        List<Integer> cartItemId = cartService.getCartItemId(user.getId(), item.getGoodId(), item.getSpecsId());
        if (cartItemId.size() == 1) {
            cartItemService.removeById(cartItemId.get(0));
        }
    }
}
