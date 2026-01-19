package com.hutb.shopping.constant;

/**
 * 购物服务常量类
 */
public class ShoppingConstant {
    
    // 库存状态常量
    public static final String STOCK_STATUS_NORMAL = "1";        // 正常
    public static final String STOCK_STATUS_OUT_OF_STOCK = "0";  // 缺货
    public static final String STOCK_STATUS_DELETED = "-1";      // 删除
    public static final String STOCK_STATUS_OFF_SHELF = "2";     // 下架
    
    // 分类状态常量
    public static final String CATEGORY_STATUS_NORMAL = "1";        // 正常
    public static final String CATEGORY_STATUS_DISABLED = "0";      // 禁用
    public static final String CATEGORY_STATUS_DELETED = "-1";      // 删除
}