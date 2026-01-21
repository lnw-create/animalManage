# 扩展UserContext功能规范

## 功能需求

### 1. UserContext增强
- 在UserContext类中添加对用户ID的存储、获取和清理功能
- 保证线程安全，每个线程独立维护用户ID信息

### 2. 拦截器增强
- 修改Intercept拦截器以支持从HTTP请求头获取用户ID
- 提供容错机制，当用户ID格式错误时不中断请求处理

### 3. 网关过滤器增强
- 修改GlobalFilter以从JWT中解析用户ID
- 将用户ID作为请求头传递给下游微服务

### 4. JWT工具类增强
- 扩展JwtUtil以支持在令牌中包含用户ID
- 提供新的创建令牌方法，支持用户名、角色和用户ID

### 5. 登录接口增强
- 修改登录接口以在JWT令牌中包含用户ID
- 确保登录响应仍然返回用户ID信息

## 接口规范

### UserContext.java
```java
/**
 * 设置当前用户的ID
 * @param userId 用户ID
 */
public static void setUserId(Long userId);

/**
 * 获取当前用户的ID
 * @return 用户ID，如果未设置则返回null
 */
public static Long getUserId();

/**
 * 移除当前用户的ID（清理ThreadLocal）
 */
public static void removeUserId();
```

### Intercept.java
```java
// 拦截器应从请求头中读取以下字段：
// - username: 用户名
// - userId: 用户ID
// - role: 用户角色
```

### GlobalFilter.java
```java
// 过滤器应将以下信息添加到请求头：
// - username: 用户名
// - userId: 用户ID
// - role: 用户角色
```

### JwtUtil.java
```java
/**
 * 生成包含用户名、角色和用户ID的JWT令牌
 * @param secretKey 密钥
 * @param timeout 过期时间（毫秒）
 * @param username 用户名
 * @param role 角色
 * @param userId 用户ID
 * @return JWT令牌字符串
 */
public static String createTokenWithUserInfo(String secretKey, long timeout, String username, String role, Long userId);
```

## 非功能性需求

### 性能
- ThreadLocal的使用不应显著影响性能
- 字符串到Long的转换应具有适当的错误处理

### 安全性
- 用户ID应通过JWT验证，防止伪造
- 请求头中的用户ID不应被客户端直接操控

### 兼容性
- 现有功能不应受到影响
- 如果JWT中没有用户ID字段，应优雅地处理（返回null）

## 错误处理

### 用户ID解析错误
- 当从请求头解析用户ID失败时，记录警告日志但不中断请求
- 当从JWT解析用户ID失败时，抛出适当的异常

### 缺失用户ID
- 如果请求头或JWT中没有用户ID，UserContext.getUserId()应返回null