package com.example.clebackend.entity.admin;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.annotations.Insert;

import java.sql.Timestamp;

@Data
public class Carousel {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message = "id不能为空")
    private Integer id;

    /**
     * 轮播图连接
     */
    @TableField("url")
    private String url;

    /**
     * 轮播图状态0 未使用 1 已使用
     */
    @NotNull(message = "轮播图状态不能为空")
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Timestamp updateTime;
}
