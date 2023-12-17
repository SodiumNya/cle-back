package com.example.clebackend.util;


import com.example.clebackend.dto.GoodDto;
import com.example.clebackend.exception.ServiceException;

public class CalculateThePrice {

    public static void calc(GoodDto orderDto){
        if(orderDto == null || orderDto.getQuantity() == null || orderDto.getPrice() == null)
            throw new ServiceException("空数据");
        orderDto.setTotalPrice(orderDto.getPrice() * orderDto.getQuantity());

    }
}
