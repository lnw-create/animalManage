package com.hutb.shopping.model.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 销售统计
 */
@Data
public class SalesStat {
    // 统计ID
    private Long statId;

    // 商品ID
    private Long productId;

    // 商品名称
    private String productName;

    // 销售数量
    private Integer salesQuantity;

    // 统计周期开始时间
    private Date startDate;

    // 统计周期结束时间
    private Date endDate;

    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
}