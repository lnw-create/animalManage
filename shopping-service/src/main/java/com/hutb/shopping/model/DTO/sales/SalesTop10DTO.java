package com.hutb.shopping.model.DTO.sales;

import lombok.Data;

@Data
public class SalesTop10DTO {
    // 商品ID
    private Long productId;

    // 商品名称
    private String productName;

    // 销售数量
    private Integer salesQuantity;
}