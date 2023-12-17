package com.example.clebackend.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;

public class ShopInfoDto {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String shopLogo;

    private String shopOwner;

    private Timestamp created;

    private int state;

    private String canteenName;
}
