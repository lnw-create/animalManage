# 扩展UserContext功能实施任务

## 任务分解

### 任务1: 修改UserContext类
- **目标**: 扩展UserContext类以支持用户ID
- **文件**: `common-utils/src/main/java/com/hutb/commonUtils/utils/UserContext.java`
- **具体工作**:
  - 添加`currentUserId` ThreadLocal变量
  - 添加`setUserId()`方法
  - 添加`getUserId()`方法
  - 添加`removeUserId()`方法
- **验证**: 确保原有功能不受影响，新功能正常工作

### 任务2: 修改Intercept拦截器
- **目标**: 使拦截器能够处理用户ID请求头
- **文件**: `common-utils/src/main/java/com/hutb/commonUtils/utils/Intercept.java`
- **具体工作**:
  - 从请求头中读取userId
  - 解析并设置到UserContext
  - 在请求完成后清理用户ID
  - 添加必要的导入语句和日志记录
- **验证**: 测试拦截器正确处理包含和不包含userId的请求

### 任务3: 修改网关的GlobalFilter
- **目标**: 使网关过滤器能够解析和传递用户ID
- **文件**: `gateway/src/main/java/com/hutb/gateway/filter/GlobalFilter.java`
- **具体工作**:
  - 从JWT中解析用户ID
  - 将用户ID添加到转发请求的头部
  - 修改请求构建逻辑以包含userId头部
- **验证**: 确保JWT解析正常，请求头正确传递

### 任务4: 修改网关的JwtUtil工具类
- **目标**: 为网关的JwtUtil添加用户ID支持
- **文件**: `gateway/src/main/java/com/hutb/gateway/utils/JwtUtil.java`
- **具体工作**:
  - 添加`createTokenWithUserInfo()`方法，支持用户ID
  - 确保新方法包含用户名、角色和用户ID
- **验证**: 测试新方法生成的JWT可以被正确解析

### 任务5: 修改common-utils的JwtUtil工具类
- **目标**: 为common-utils的JwtUtil添加用户ID支持
- **文件**: `common-utils/src/main/java/com/hutb/commonUtils/utils/JwtUtil.java`
- **具体工作**:
  - 添加`createTokenWithUserInfo()`方法，支持用户ID
  - 确保新方法包含用户名、角色和用户ID
- **验证**: 测试新方法生成的JWT可以被正确解析

### 任务6: 修改用户服务的登录实现
- **目标**: 使登录接口在JWT中包含用户ID
- **文件**: `user-service/src/main/java/com/hutb/user/service/impl/UserServiceImpl.java`
- **具体工作**:
  - 修改login方法，使用包含用户ID的新JWT方法
  - 确保JWT中包含用户ID信息
- **验证**: 测试登录接口返回的JWT包含用户ID且可以被正确解析

### 任务7: 更新WebMvcConfig配置
- **目标**: 确保拦截器被正确注册
- **文件**: `common-utils/src/main/java/com/hutb/commonUtils/utils/WebMvcConfig.java`
- **具体工作**:
  - 检查拦截器配置是否正确
  - 如有必要，更新配置以确保拦截器正常工作
- **验证**: 确保拦截器在请求处理过程中被调用

### 任务8: 全面测试
- **目标**: 验证整个流程正常工作
- **具体工作**:
  - 执行完整的端到端测试：登录 -> JWT生成 -> 网关处理 -> 微服务接收
  - 验证在微服务中可以通过UserContext获取用户ID
  - 测试各种边界情况和错误处理
- **验证**: 确保所有功能按预期工作，无回归问题