package com.hutb.commonUtils.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器
 * 拦截网关发送到微服务的请求
 */
@Component
public class Intercept implements HandlerInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(Intercept.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头的用户信息
        String username = request.getHeader("username");
        String userIdStr = request.getHeader("userId");

        //2.存储到threadLocal
        if (username != null && !username.isEmpty()){
            UserContext.setUsername(username);
        }
        
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdStr);
                UserContext.setUserId(userId);
            } catch (NumberFormatException e) {
                // 记录错误日志但不中断请求
                log.warn("Invalid userId header: {}", userIdStr);
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
        UserContext.removeUserId();
    }
}
