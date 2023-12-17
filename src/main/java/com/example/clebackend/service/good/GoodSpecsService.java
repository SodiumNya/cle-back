package com.example.clebackend.service.good;

import com.example.clebackend.dto.OrderDto;
import com.example.clebackend.dto.ProductCommentsDto;
import com.example.clebackend.entity.product.Goods;
import com.example.clebackend.entity.product.GoodsSpecs;

import java.util.List;

public interface GoodSpecsService {

    GoodsSpecs getPriceBySpec(String spec);


}
