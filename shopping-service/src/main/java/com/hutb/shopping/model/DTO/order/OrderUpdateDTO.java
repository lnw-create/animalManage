package com.hutb.shopping.model.DTO.order;

import lombok.Data;

@Data
public class OrderUpdateDTO {

    private Long id;

    // 订单状态
    private String status;

    // 收货地址
    private String shippingAddress;
}