package com.hutb.commonUtils.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    /**
     * 生成jwt令牌
     */
    public static String createJwt(String secretKey, long timeout, Map<String, Object> claims){
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + timeout))
                .signWith(key)
                .compact();
    }

    /**
     * 解析jwt令牌
     */
    public static Map<String,Object> parseJwt(String token, String secretKey){
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }
    
    /**
     * 生成包含用户名和角色的JWT令牌
     */
    public static String createTokenWithRole(String secretKey, long timeout, String username, String role) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);
        claims.put("timestamp", System.currentTimeMillis());
        
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + timeout))
                .signWith(key)
                .compact();
    }
    
    /**
     * 生成包含用户名、角色和用户ID的JWT令牌
     */
    public static String createTokenWithUserInfo(String secretKey, long timeout, String username, String role, Long userId) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);
        claims.put("userId", userId);
        claims.put("timestamp", System.currentTimeMillis());
        
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + timeout))
                .signWith(key)
                .compact();
    }
}
