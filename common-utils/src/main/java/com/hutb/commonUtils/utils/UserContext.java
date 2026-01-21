package com.hutb.commonUtils.utils;

/**
 *  获取用户信息工具类
 */
public class UserContext {
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

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
    
    //设置用户ID
    public static void setUserId(Long userId) {
        currentUserId.set(userId);
    }
    //获取用户ID
    public static Long getUserId(){
        return currentUserId.get();
    }
    //删除用户ID
    public static void removeUserId(){
        currentUserId.remove();
    }
}
