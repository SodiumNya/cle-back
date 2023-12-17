package com.example.clebackend.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clebackend.entity.product.Category;
import com.example.clebackend.entity.product.GoodsSpecs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
