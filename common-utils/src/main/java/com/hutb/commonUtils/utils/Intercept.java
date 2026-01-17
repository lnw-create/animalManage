package com.hutb.commonUtils.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器
 * 拦截网关发送到微服务的请求
 */
@Component
public class Intercept implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头的用户信息
        String username = request.getHeader("username");

        //2.存储到threadLocal
        if (username != null && !username.isEmpty()){
            UserContext.setUsername(username);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }
}
