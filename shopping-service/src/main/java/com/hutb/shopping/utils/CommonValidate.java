package com.hutb.shopping.utils;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.CommonUtils;
import com.hutb.shopping.model.DTO.CategoryDTO;
import com.hutb.shopping.model.DTO.StockDTO;
import com.hutb.shopping.model.DTO.order.OrderCreateDTO;
import com.hutb.shopping.model.DTO.order.OrderUpdateDTO;

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

    /**
     * 验证订单信息
     * @param orderCreateDTO 订单信息
     */
    public static void validateOrder(OrderCreateDTO orderCreateDTO) {
        if (orderCreateDTO == null) {
            throw new CommonException("订单信息不能为空");
        }
        if (orderCreateDTO.getUserId() == null || orderCreateDTO.getUserId() <= 0) {
            throw new CommonException("用户ID不能为空");
        }
        if (CommonUtils.stringIsBlank(orderCreateDTO.getShippingAddress())) {
            throw new CommonException("收货地址不能为空");
        }
    }

    /**
     * 验证订单更新信息
     * @param orderUpdateDTO 订单信息
     */
    public static void validateOrderUpdate(OrderUpdateDTO orderUpdateDTO) {
        if (orderUpdateDTO == null) {
            throw new CommonException("订单信息不能为空");
        }
        if (orderUpdateDTO.getId() == null || orderUpdateDTO.getId() <= 0) {
            throw new CommonException("订单ID不能为空");
        }
    }
}