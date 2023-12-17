package com.example.clebackend.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clebackend.dto.OrderDetailDto;
import com.example.clebackend.dto.OrderInfoDto;
import com.example.clebackend.entity.order.Order;
import com.example.clebackend.vo.OrderInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<OrderInfoDto> getInfoByUserAccount(@Param("account") String account, @Param("statue") Integer statue, @Param("asc") Integer asc);
    List<OrderInfoDto> getInfoByShopId(@Param("shopId") Integer shopId, @Param("statue") Integer statue, @Param("asc") Integer asc);
    OrderDetailDto getDetail(String orderSn);





}
