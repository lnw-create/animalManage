# 扩展UserContext功能设计方案

## 设计概述

扩展UserContext工具类，使其能够同时管理用户名和用户ID，通过ThreadLocal在线程范围内共享用户上下文信息。

## 组件设计

### 1. UserContext.java (common-utils模块)

```java
public class UserContext {
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    // 原有方法保持不变
    public static void setUsername(String username) { ... }
    public static String getUsername() { ... }
    public static void remove() { ... }

    // 新增方法
    public static void setUserId(Long userId) { ... }
    public static Long getUserId() { ... }
}
```

### 2. Intercept.java (common-utils模块)

修改拦截器以同时处理用户名和用户ID：

```java
@Component
public class Intercept implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头的用户信息
        String username = request.getHeader("username");
        String userIdStr = request.getHeader("userId");

        // 存储到ThreadLocal
        if (username != null && !username.isEmpty()) {
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
        UserContext.removeUserId(); // 清除用户ID
    }
}
```

### 3. GlobalFilter.java (gateway模块)

修改网关过滤器以传递用户ID：

```java
@Component
public class GlobalFilter implements org.springframework.cloud.gateway.filter.GlobalFilter, Ordered {
    // ...
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // ... 现有代码 ...

        String username = claims.get("username").toString();
        String role = claims.get("role").toString();
        Long userId = ((Number) claims.get("userId")).longValue(); // 从JWT中获取用户ID

        // 修改发送给微服务的请求头，加入用户信息
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(builder -> builder
                    .header("username", username)
                    .header("userId", String.valueOf(userId))
                    .header("role", role))
                .build();

        return chain.filter(modifiedExchange);
    }
}
```

### 4. JwtUtil.java (gateway模块和common-utils模块)

修改JWT工具类以支持用户ID：

```java
public class JwtUtil {
    // ... 现有方法 ...

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
```

### 5. 登录接口 (user-service模块)

修改UserService.login方法以在JWT中包含用户ID：

```java
@Override
public LoginResponse login(String username, String password) {
    // ... 现有验证逻辑 ...

    // 生成JWT token，包含用户ID
    String token = com.hutb.commonUtils.utils.JwtUtil.createTokenWithUserInfo(
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC8FgCz6/n59Z6VX5xtzvQ4aCU2oIqxERUd/Qk5uVQ2WMZS6OfmvmP3ZQ+Oo+2y1E+W8yaZTSVXVI2ztNxJJNkMSQX+uCv3+6FbX6W//R/1DhXD7XkXiPx2+6NgljEiKCw+7g1y4UlywX1m0JDlPSqphGyWTybD4m37Xy/cJwIDAQAB",
        timeout,
        user.getUsername(),
        user.getRole(),
        user.getId()
    );
    
    return new LoginResponse(user.getId(), user.getUsername(), user.getRole(), token);
}
```

## 数据流设计

1. 用户登录时，系统生成包含用户ID的JWT令牌
2. 网关解析JWT，提取用户ID并将其作为请求头传递给微服务
3. 微服务拦截器接收用户ID并存储到ThreadLocal
4. 业务逻辑可通过UserContext获取用户ID

## 向后兼容性

所有修改都将保持向后兼容：
- 现有的UserContext.getUsername()方法不受影响
- 如果请求头中没有userId，则不会设置用户ID，避免影响现有功能
- JWT解析逻辑会处理可能缺失的userId字段