package com.hutb.shopping.utils;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.CommonUtils;
import com.hutb.shopping.model.DTO.CategoryDTO;
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
    
    /**
     * 验证分类信息
     * @param categoryDTO 分类信息
     */
    public static void validateCategory(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            throw new CommonException("分类信息不能为空");
        }
        if (CommonUtils.stringIsBlank(categoryDTO.getName())) {
            throw new CommonException("分类名称不能为空");
        }
        if (categoryDTO.getName().length() > 100) {
            throw new CommonException("分类名称长度不能超过100个字符");
        }
        if (categoryDTO.getDescription() != null && categoryDTO.getDescription().length() > 500) {
            throw new CommonException("分类描述长度不能超过500个字符");
        }
        if (categoryDTO.getSort() != null && categoryDTO.getSort() < 0) {
            throw new CommonException("排序值不能为负数");
        }
    }
}