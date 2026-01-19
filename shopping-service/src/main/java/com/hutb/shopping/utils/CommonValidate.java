package com.hutb.shopping.utils;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.CommonUtils;
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
        if (CommonUtils.stringIsBlank(stockDTO.getProductName())) {
            throw new CommonException("商品名称不能为空");
        }
        if (stockDTO.getPrice() != null && stockDTO.getPrice() < 0) {
            throw new CommonException("商品积分价格不能为负数");
        }
        if (CommonUtils.stringIsBlank(stockDTO.getImage())) {
            throw new CommonException("商品图片路径不能为空");
        }
        if (stockDTO.getQuantity() == null || stockDTO.getQuantity() < 0) {
            throw new CommonException("库存数量不能为负数");
        }
        if (stockDTO.getCategoryId() != null && stockDTO.getCategoryId() <= 0) {
            throw new CommonException("商品分类ID不能为空");
        }
    }
}