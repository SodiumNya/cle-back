package com.example.clebackend.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.clebackend.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductVo {

    private String location;

    private List<Product> productList;


}
