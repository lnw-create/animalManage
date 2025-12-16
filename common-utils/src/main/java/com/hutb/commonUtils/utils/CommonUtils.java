package com.hutb.commonUtils.utils;

/**
 * 通用工具类
 */
public class CommonUtils {
    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static Boolean stringIsBlank(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 校验用户是否有权限
     * @param userId
     * @return
     */
    public static void permissionValidate(Long userId) {
        //todo 校验用户是否有权限

        //1. 获取当前用户信息，判断是否有权限

        //2. 如果没有权限，抛出异常
    }
}