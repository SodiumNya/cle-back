package com.example.clebackend.controller.order;


import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.common.RestBean;
import com.example.clebackend.common.UserRole;
import com.example.clebackend.entity.order.Order;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.request.OrderItemRequest;
import com.example.clebackend.request.OrderRequest;
import com.example.clebackend.service.order.OrderService;
import com.example.clebackend.util.TokenUtils;
import com.example.clebackend.vo.OrderDetailVo;
import com.example.clebackend.vo.OrderInfoVo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {

    @Resource
    OrderService orderService;

    @PostMapping("generate")
    public RestBean<String> generateOrder(@RequestBody @Valid OrderRequest ordersReq){

        User user = TokenUtils.getCurrentUser();
        if(user == null)
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "未登录");

        List<OrderItemRequest> reqOrder = ordersReq.getOrder();

        Integer res = orderService.generateOrder(reqOrder, user);

        if(res == -1) return RestBean.error(ResponseCode.FAIL.getCode(), "请先完善用户信息");

        return RestBean.success("支付成功");
    }


    @GetMapping("info/{statue}/{asc}")
    public RestBean<List<OrderInfoVo>> getOrderInfo(@PathVariable Integer statue, @PathVariable Integer asc){

        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "未登录");

        List<OrderInfoVo> orderList = orderService.getInfoByUserAccount(user.getAccount(), statue, asc);
        assert orderList != null;

        return RestBean.success(orderList);
    }

    @GetMapping("shop/info/{shopId}/{statue}/{asc}")
    public RestBean<List<OrderInfoVo>> getOrderInfoByShop(@PathVariable Integer statue, @PathVariable Integer asc, @PathVariable Integer shopId){

        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "未登录");

        if(!UserRole.SHOP_OWNER.getRoleName().equals(user.getRole()))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        List<OrderInfoVo> orderList = orderService.getInfoByShopId(shopId, statue, asc);
        assert orderList != null;

        return RestBean.success(orderList);
    }

    @GetMapping("detail/{orderSn}")
    public RestBean<OrderDetailVo> getOrderDetail(@PathVariable String orderSn){
        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "未登录");

        OrderDetailVo orderDetail = orderService.getDetail(orderSn);
        assert orderDetail != null;

        return RestBean.success(orderDetail);

    }


    @PutMapping("receive")
    public RestBean<String> receiveOrder(@Nonnull @RequestBody Order order){
        User user = TokenUtils.getCurrentUser();
        if(user == null || !UserRole.SHOP_OWNER.getRoleName().equals(user.getRole()))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        Integer res = orderService.updateOrderStatue(order.getOrderSn(), 2);

        if(res <= 0)
            return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");

        return RestBean.success("更新成功");
    }

    @PutMapping("waiting_for_pickup")
    public RestBean<String> waitingPickUpOrder(@Nonnull @RequestBody Order order){
        User user = TokenUtils.getCurrentUser();
        if(user == null || !UserRole.SHOP_OWNER.getRoleName().equals(user.getRole()))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        Integer res = orderService.updateOrderStatue(order.getOrderSn(), 3);

        if(res <= 0)
            return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");

        return RestBean.success("更新成功");
    }


    @PutMapping("finish")
    @Transactional
    public RestBean<String> finishOrder(@Nonnull @RequestBody Order order){
        User user = TokenUtils.getCurrentUser();
        if(user == null || !UserRole.CUSTOMER.getRoleName().equals(user.getRole()))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        Integer res = orderService.updateOrderStatue(order.getOrderSn(), 4);

        if(res <= 0) return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");

        return RestBean.success("更新成功");
    }

    @PutMapping("cancel")
    public RestBean<String> cancelOrder(@Nonnull  @RequestBody Order order){
        User user = TokenUtils.getCurrentUser();
        if (user == null) {
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        }

        Order resData = orderService.getOrderStatus(order.getOrderSn());
        assert resData != null;

        if (!checkUserRolePermission(user, resData)) {
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        }

        Integer res = orderService.updateOrderStatue(order.getOrderSn(), 5);

        if (res <= 0) {
            return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");
        }

        return RestBean.success("更新成功");
    }

    // 提取权限验证逻辑
    private boolean checkUserRolePermission(User user, Order order) {
        int orderStatus = order.getStatue();

        if (UserRole.CUSTOMER.getRoleName().equals(user.getRole())) {
            return orderStatus == 0 || orderStatus == 1;
        } else if (UserRole.SHOP_OWNER.getRoleName().equals(user.getRole())) {
            return orderStatus == 1 || orderStatus == 2;
        }

        return false;
    }
}
