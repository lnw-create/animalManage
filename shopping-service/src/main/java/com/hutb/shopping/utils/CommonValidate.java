package com.hutb.shopping.utils;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.shopping.model.DTO.StockDTO;

public class CommonValidate {
    
    /**
     * 验证库存信息
     * @param stockDTO 库存信息
     */
    public static void validateStock(StockDTO stockDTO) {
        if (stockDTO == null) {
            throw new CommonException("库存信息不能为空");
        }
        if (stockDTO.getProductName() == null || stockDTO.getProductName().trim().isEmpty()) {
            throw new CommonException("商品名称不能为空");
        }
        if (stockDTO.getQuantity() == null || stockDTO.getQuantity() < 0) {
            throw new CommonException("库存数量不能为负数");
        }
    }
}