package com.example.clebackend.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.clebackend.common.RestBean;
import com.example.clebackend.entity.admin.Carousel;
import com.example.clebackend.mapper.admin.CarouselMapper;
import com.example.clebackend.vo.CarouselVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

public class CommonController {

    @Resource
    CarouselMapper carouselMapper;

    @GetMapping("get/carousel")
    public RestBean<List<CarouselVo>> getCarousel(){
        QueryWrapper<Carousel> queryWrapper = new QueryWrapper<>();
        IPage<Carousel> page = new Page<>(1, 5);
        List<Carousel> records = carouselMapper.selectPage(page, queryWrapper).getRecords();
        List<CarouselVo> vo = records.stream().map(carousel -> {
            CarouselVo carouselVo = new CarouselVo();
            BeanUtil.copyProperties(carousel, carouselVo);
            return carouselVo;
        }).toList();

        return RestBean.success(vo);
    }
}
