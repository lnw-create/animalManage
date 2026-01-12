package com.hutb.commonUtils.utils;

/**
 *  获取用户信息工具类
 */
public class UserContext {
    private static final ThreadLocal<Long> currentUser = new ThreadLocal<>();

    //设置id
    public static void setUserId(Long userId) {
        currentUser.set(userId);
    }
    //获取id
    public static Long getUserId(){
        return currentUser.get();
    }
    //删除id
    public static void remove(){
        currentUser.remove();
    }
}
