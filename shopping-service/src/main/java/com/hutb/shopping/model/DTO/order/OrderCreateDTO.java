package com.hutb.shopping.model.DTO.order;

import lombok.Data;

@Data
public class OrderCreateDTO {
    // 用户ID
    private Long userId;

    // 订单状态
    private String status = "pending_payment";

    // 收货地址
    private String shippingAddress;

    // 商品ID
    private Long productId;

    // 商品名称
    private String productName;

    // 单价（积分）
    private Integer price;
}