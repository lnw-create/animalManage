package com.hutb.user.model.vo;

import lombok.Data;

/**
 * 统一登录请求对象
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}