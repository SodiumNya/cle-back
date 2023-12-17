package com.example.clebackend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.clebackend.entity.Product;
import com.example.clebackend.entity.product.Goods;
import com.example.clebackend.entity.shop.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface ShopMapper extends BaseMapper<Shop> {

    @Select("select distinct canteen_name from shop")
    public List<String> getLocationList();




}
