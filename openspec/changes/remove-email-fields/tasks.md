# 移除用户服务中邮件相关字段的任务清单

## 任务列表

### 任务1: 更新User实体类
- 文件: `user-service/src/main/java/com/hutb/user/model/pojo/User.java`
- 操作: 移除private String email;字段
- 预估时间: 5分钟

### 任务2: 更新UserDTO类
- 文件: `user-service/src/main/java/com/hutb/user/model/DTO/UserDTO.java`
- 操作: 移除private String email;字段
- 预估时间: 5分钟

### 任务3: 更新Admin实体类
- 文件: `user-service/src/main/java/com/hutb/user/model/pojo/Admin.java`
- 操作: 移除private String email;字段
- 预估时间: 5分钟

### 任务4: 更新AdminDTO类
- 文件: `user-service/src/main/java/com/hutb/user/model/DTO/AdminDTO.java`
- 操作: 移除private String email;字段
- 预估时间: 5分钟

### 任务5: 更新userMapper接口
- 文件: `user-service/src/main/java/com/hutb/user/mapper/userMapper.java`
- 操作: 
  - 修改@Insert注解中的SQL语句，移除email相关部分
  - 修改@Update注解中的SQL语句，移除email相关部分
- 预估时间: 10分钟

### 任务6: 更新employeeMapper接口
- 文件: `user-service/src/main/java/com/hutb/user/mapper/employeeMapper.java`
- 操作:
  - 修改@Insert注解中的SQL语句，移除email相关部分
  - 修改@Update注解中的SQL语句，移除email相关部分
- 预估时间: 10分钟

### 任务7: 更新CommonValidate工具类
- 文件: `user-service/src/main/java/com/hutb/user/utils/CommonValidate.java`
- 操作:
  - 修改adminValidate方法，移除email相关验证
  - 修改userValidate方法，移除email相关验证
- 预估时间: 10分钟

### 任务8: 更新API规范文档
- 文件: `openspec/changes/implement-user-registration-only/specs/user-service/spec.md`
- 操作: 移除所有关于email字段的描述
- 预估时间: 10分钟

### 任务9: 更新设计文档
- 文件: `openspec/changes/implement-user-registration-only/design.md`
- 操作: 移除所有关于email字段的描述
- 预估时间: 10分钟

## 依赖关系

- 任务1-4可以并行执行
- 任务5-6可以并行执行
- 任务7依赖于任务1-4的完成
- 任务8-9可以在任何时间执行

## 验证步骤

1. 编译整个项目，确保没有编译错误
2. 运行单元测试，确保所有测试通过
3. 验证API接口行为是否符合预期
4. 检查数据库操作是否正常工作