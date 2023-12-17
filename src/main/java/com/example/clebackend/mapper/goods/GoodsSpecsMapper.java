package com.example.clebackend.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clebackend.entity.product.GoodsSpecs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsSpecsMapper extends BaseMapper<GoodsSpecs> {
    Integer updateGoodsSpecs(GoodsSpecs goodsSpecs);
}
