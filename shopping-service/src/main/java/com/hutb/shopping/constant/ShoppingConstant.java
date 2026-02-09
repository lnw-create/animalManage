package com.hutb.shopping.constant;

public class ShoppingConstant {
    // 订单状态常量
    public static final String ORDER_STATUS_PENDING_PAYMENT = "pending_payment"; // 待付款
    public static final String ORDER_STATUS_PAID = "paid"; // 已付款
    public static final String ORDER_STATUS_SHIPPED = "shipped"; // 已发货
    public static final String ORDER_STATUS_DELIVERED = "delivered"; // 已签收
    public static final String ORDER_STATUS_CANCELLED = "cancelled"; // 已取消
    public static final String ORDER_STATUS_REFUNDED = "refunded"; // 已退款
    public static final String ORDER_STATUS_DELETED = "deleted"; // 已删除（软删除）

    // 商品分类状态常量
    public static final String CATEGORY_STATUS_NORMAL = "1"; // 正常
    public static final String CATEGORY_STATUS_DISABLED = "0"; // 禁用
    public static final String CATEGORY_STATUS_DELETED = "-1"; // 删除

    // 库存状态常量
    public static final String STOCK_STATUS_NORMAL = "1"; // 正常
    public static final String STOCK_STATUS_OUT_OF_STOCK = "0"; // 缺货
    public static final String STOCK_STATUS_DELETED = "-1"; // 删除
    public static final String STOCK_STATUS_OFF_SHELF = "2"; // 下架
}