package com.example.clebackend.dto;

import com.example.clebackend.entity.product.GoodsSpecs;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 添加商品的dto
 * @author wang
 */
@Data
public class AddGoodsDto {

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

    private List<AddGoodsSpecsDto> specsList;

//    private List<MultipartFile> pictureList;
}
