package com.hutb.shopping.model.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 订单
 */
@Data
public class Order {
    // 订单ID
    private Long id;

    // 用户ID
    private Long userId;

    // 订单号
    private String orderNumber;

    // 订单总积分（price * 1）
    private Integer totalIntegral;

    // 订单状态
    private String status;

    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;

    // 收货地址
    private String shippingAddress;
    
    // 商品ID
    private Long productId;
    
    // 商品名称
    private String productName;
    
    // 单价（积分）
    private Integer price;
}