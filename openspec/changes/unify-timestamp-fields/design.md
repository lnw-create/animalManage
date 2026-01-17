# 统一时间戳和用户字段命名规范详细设计

## 变更说明

本设计文档详细说明如何统一各微服务中实体类的时间戳和用户字段命名。

## 当前状态分析

### user-service
- User.java: `createTime`, `updateTime`, `createUser`, `modifiedUser` (不一致)
- VolunteerDTO.java: `createTime`, `updateTime`, `createUser`, `modifiedUser` (不一致)

### shopping-service
- Stock.java: `createTime`, `updateTime`, `createUser`, `updateUser` (一致)

### volunteerActivity-service
- VolunteerActivityDTO.java: `createTime`, `updateTime`, `createUser`, `modifiedUser` (不一致)

## 具体变更内容

### 1. user-service 实体类变更

#### User.java
- 将 `modifiedUser` 字段重命名为 `updateUser`

#### UserDTO.java
- 将 `modifiedUser` 字段重命名为 `updateUser`

#### VolunteerDTO.java
- 将 `modifiedUser` 字段重命名为 `updateUser`

### 2. user-service Mapper 接口和实现变更

#### userMapper.java
- 更新SQL语句中的字段名
- 修改方法签名中的参数引用

### 3. user-service Service 实现变更

#### UserServiceImpl.java
- 更新字段访问名称

### 4. volunteerActivity-service 实体类变更

#### VolunteerActivityDTO.java
- 将 `modifiedUser` 字段重命名为 `updateUser`

### 5. volunteerActivity-service Mapper 接口和实现变更

#### volunteerActivityMapper.java
- 更新SQL语句中的字段名
- 修改方法签名中的参数引用

### 6. volunteerActivity-service Service 实现变更

#### VolunteerActivityServiceImpl.java
- 更新字段访问名称

## 数据库兼容性处理

如果数据库中字段名为 `modified_user`，需要考虑以下策略之一：
1. 同时支持两个字段名（向后兼容）
2. 提供数据库迁移脚本
3. 通过MyBatis映射处理

建议采用MyBatis结果映射来处理字段名差异，避免直接修改数据库结构。

## 测试策略

1. 单元测试：验证实体类字段名称更改
2. 集成测试：验证数据库读写操作
3. 端到端测试：验证完整业务流程