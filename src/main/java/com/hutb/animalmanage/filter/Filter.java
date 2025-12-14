package com.hutb.animalmanage.filter;

import com.hutb.animalmanage.utils.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
@WebFilter("/*") //拦截所有请求
public class Filter implements jakarta.servlet.Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //1.获取请求信息和响应信息
        HttpServletRequest  request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //2.获取请求的URI
        String requestURI = request.getRequestURI(); // /rent/login

        //3.判断请求是否包含login和register，如果包含，则放行
        if (requestURI.contains("/login") || requestURI.contains("/register")){
            filterChain.doFilter(request, response);
            return;
        }

        //4.从请求头中获取令牌token
        String token = request.getHeader("token");

        //5.判断token是否存在，如果不存在，则返回未登录状态码
        if (token == null || token.isEmpty()){
            response.setStatus(401);
            return;
        }

        //6.解析token，如果解析失败，则返回未登录状态码,即token错误
        try {
            JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.info("解析令牌失败,令牌不正确");
        }

        //7.放行
        filterChain.doFilter(request, response);
    }
}