package com.hutb.user.model.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一登录响应对象
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {
    private Long userId;
    private String username;
    private String role;
    private String token;
}