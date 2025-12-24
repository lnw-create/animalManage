package com.hutb.user.constant;

/**
 * 管理员常量类
 */
public class UserCommonConstant {
    /**
     * 超管账号
     */
    public static final String ADMIN_ACCOUNT = "admin";
    /**
     * 超管角色
     */
    public static final String ADMIN_ROLE_SUPER = "super_admin";

    /**
     * 普通管理员角色
     */
    public static final String ADMIN_ROLE_NORMAL = "normal_admin";


    /**
     * 超管密码
     */
    public static final String ADMIN_PASSWORD = "123456";

    /**
     * 管理员状态
     */
    // 启用
    public static final String ADMIN_STATUS_ENABLE = "1";
    // 禁用
    public static final String ADMIN_STATUS_DISABLE = "0";
    // 删除
    public static final String ADMIN_STATUS_DELETE = "-1";

    /**
     * 用户状态
     */
    // 启用
    public static final String USER_STATUS_ENABLE = "1";
    // 删除
    public static final String USER_STATUS_DELETE = "-1";
}
