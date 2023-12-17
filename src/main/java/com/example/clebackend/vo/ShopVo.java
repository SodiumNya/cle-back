package com.example.clebackend.vo;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wangqi
 * 店铺表
 */
@Data
public class ShopVo {
    @NotNull(message = "id 不能为空")
    private Integer id;

    private String name;

    private String shopLogo;

    private String canteenName;

    private String phone;
}
