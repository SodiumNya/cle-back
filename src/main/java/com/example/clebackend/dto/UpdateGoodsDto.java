package com.example.clebackend.dto;

import lombok.Data;

import java.util.List;


/**
 * 修改商品的dto
 * @author wang
 */
@Data
public class UpdateGoodsDto {

    private Integer id;

    private Integer categoryId;

    private String categoryName;

    private String name;

//    private MultipartFile mainImage;

    private String detail;

    private Integer shopId;

    private String attributeList;

    private String startTime;

    private String endTime;

    private Integer allDaySale;

    private String specsListJson;

    private List<UpdateGoodsSpecsDto> specsList;

    private List<Integer> deletePicture;
}
