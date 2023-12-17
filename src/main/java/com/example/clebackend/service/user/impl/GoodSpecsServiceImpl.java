package com.example.clebackend.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.clebackend.dto.OrderDto;
import com.example.clebackend.dto.ProductCommentsDto;
import com.example.clebackend.entity.product.Goods;
import com.example.clebackend.entity.product.GoodsSpecs;
import com.example.clebackend.mapper.goods.GoodsSpecsMapper;
import com.example.clebackend.mapper.user.ProductMapper;
import com.example.clebackend.service.good.GoodSpecsService;
import com.example.clebackend.service.good.ProductService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GoodSpecsServiceImpl implements GoodSpecsService {

    @Resource
    GoodsSpecsMapper goodsSpecsMapper;


    @Override
    public GoodsSpecs getPriceBySpec(String specs) {
        QueryWrapper<GoodsSpecs> goodsSpecsQueryWrapper = new QueryWrapper<>();
        goodsSpecsQueryWrapper.eq("specs", specs);

        return goodsSpecsMapper.selectOne(goodsSpecsQueryWrapper);
    }
}
