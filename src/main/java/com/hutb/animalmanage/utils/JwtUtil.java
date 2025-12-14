package com.hutb.animalmanage.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    // 定义密钥（建议使用更安全的密钥生成方式）
    private static final String SECRET_KEY = "aXRoZWltYQqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"; // Base64 编码的密钥
    private static final byte[] KEY_BYTES = Keys.hmacShaKeyFor(SECRET_KEY.getBytes()).getEncoded(); // 密钥, 使用密钥生成器生成
    // 定义过期时间
    private static final long EXPIRATION_TIME = 3600 * 1000; // 1小时

    /*
     * 生成JWT令牌
     * @param claims 令牌中包含的信息
     * @return 生成的JWT令牌字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder() // 创建JWT构建器
                .setClaims(claims) // 设置令牌中包含的信息
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置令牌过期时间
                .signWith(Keys.hmacShaKeyFor(KEY_BYTES)) // 使用 withKey 替代 signWith  使用密钥生成器生成
                .compact(); // 生成JWT令牌字符串
    }

    /*
     * 解析JWT令牌
     * @param token 需要解析的JWT令牌字符串
     * @return 解析后的令牌信息
     */
    public static Map<String, Object> parseToken(String token) {
        return Jwts.parserBuilder() // 创建 JwtParserBuilder 实例
                .setSigningKey(KEY_BYTES) // 设置签名密钥
                .build() // 构建 JwtParser 实例
                .parseClaimsJws(token) // 解析 JWT 令牌
                .getBody(); // 获取载荷部分
    }
}
