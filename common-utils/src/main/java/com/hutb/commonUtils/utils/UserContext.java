package com.hutb.commonUtils.utils;

/**
 *  获取用户信息工具类
 */
public class UserContext {
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    //设置用户名
    public static void setUsername(String username) {
        currentUser.set(username);
    }
    //获取用户名
    public static String getUsername(){
        return currentUser.get();
    }
    //删除用户名
    public static void remove(){
        currentUser.remove();
    }
}
