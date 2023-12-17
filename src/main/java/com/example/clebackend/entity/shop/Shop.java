package com.example.clebackend.entity.shop;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author wangqi
 * 店铺表
 */
@Data
@TableName("shop")
public class Shop {

    /**
     * 主键 id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 店铺名
     */
    private String name;

    /**
     * 商店logo
     */
    private String shopLogo;

    /**
     * 所有人
     */
    private String shopOwner;

    /**
     * 创建时间
     */
    private Timestamp created;

    /**
     * 状态 (营业 休息)
     */
    private Integer state;

    /**
     * 食堂名字
     */
    private String canteenName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邀请码
     */
    private String invitationCode;
}
