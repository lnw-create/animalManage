package com.hutb.commonUtils.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
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
}
