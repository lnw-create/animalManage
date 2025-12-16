package com.hutb.gateway.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {
    /**
     * 生成jwt令牌
     */
    public static String createJwt(String secretKey, long timeout, Map<String, Object> claims){
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + timeout))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }

    /**
     * 解析jwt令牌
     */
    public static Map<String,Object> parseJwt(String token, String secretKey){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
