# 移除用户服务中邮件相关字段的设计文档

## 设计目标

移除用户服务中所有与邮件相关的字段，简化数据模型，减少不必要的数据存储和验证逻辑。

## 变更设计

### 实体类设计变更
- User实体类中移除email字段
- Admin实体类中移除email字段

### DTO类设计变更
- UserDTO类中移除email字段
- AdminDTO类中移除email字段

### 数据访问层设计变更
- 修改userMapper接口中的方法签名和SQL语句，排除email字段
- 修改employeeMapper接口中的方法签名和SQL语句，排除email字段

### 业务逻辑层设计变更
- 修改CommonValidate类，移除对email字段的验证逻辑
- 更新相关服务类，确保不再使用email字段

### API接口设计变更
- 更新API接口定义，不再包含email字段
- 修改请求和响应模型，移除email字段

## 实现步骤

### 步骤1: 实体类修改
1. 从User类中删除email字段
2. 从Admin类中删除email字段

### 步骤2: DTO类修改
1. 从UserDTO类中删除email字段
2. 从AdminDTO类中删除email字段

### 步骤3: 数据访问层修改
1. 修改userMapper中的SQL语句，排除email字段
2. 修改employeeMapper中的SQL语句，排除email字段

### 步骤4: 验证逻辑修改
1. 修改CommonValidate类，移除email验证逻辑

### 步骤5: 服务层适配
1. 更新相关服务实现，确保兼容字段移除

## 错误处理

移除email字段后，不再需要对email字段进行验证，因此相关的验证错误也不再出现。

## 测试策略

- 单元测试验证实体类和DTO类中不再包含email字段
- 集成测试验证数据访问层正确处理没有email字段的情况
- API测试验证接口不再接受或返回email字段

## 部署说明

由于这是一个破坏性变更，需要：
1. 在非高峰时段部署
2. 提前通知所有API消费者
3. 准备回滚计划