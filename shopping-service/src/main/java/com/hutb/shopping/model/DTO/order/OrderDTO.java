package com.hutb.shopping.model.DTO.order;

import lombok.Data;

@Data
public class OrderDTO {
    // 订单ID - 更新时需要，创建时为空
    private Long orderId;

    // 用户ID - 创建时需要
    private Long userId;

    // 商品ID - 创建时需要
    private Long productId;

    // 商品名称 - 创建时需要
    private String productName;

    // 单价（积分）- 创建时需要
    private Integer price;

    // 订单状态 - 可选，创建时默认为pending_payment
    private String status = "pending_payment";

    // 收货地址 - 创建和更新时都需要
    private String shippingAddress;
}