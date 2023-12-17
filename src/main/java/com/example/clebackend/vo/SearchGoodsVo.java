package com.example.clebackend.vo;

import com.example.clebackend.dto.ShopGoodsDto;
import lombok.Data;

import java.util.List;

@Data
public class SearchGoodsVo {

    private String canteenName;

    private List<ShopGoodsVo> goodsList;

}
