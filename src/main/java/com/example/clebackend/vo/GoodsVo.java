package com.example.clebackend.vo;

import com.example.clebackend.entity.product.Goods;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author wangqi
 * 商品表
 */
@Data
public class GoodsVo {

    private Integer id;

    private Integer shopId;

    private Integer categoryId;

    private String name;

    private String mainImage;

    private String detail;

    private Double score;

    private Long sale;

    private Map<String, List<String>> specs;

    private String startSale;

    private String endSale;

    private Integer allDaySale;

    private Integer saleStatue;




}
