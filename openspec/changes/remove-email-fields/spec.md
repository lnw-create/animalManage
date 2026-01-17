# 移除用户服务中的邮件相关字段

## 变更概述

本变更旨在从用户服务中完全移除所有与邮件相关的字段和功能，包括但不限于实体类中的email字段、数据库中的email列、API接口中的email参数以及相关的验证逻辑。

## 变更范围

### 影响的模块
- 用户实体 (User)
- 管理员实体 (Admin)
- 用户数据传输对象 (UserDTO)
- 管理员数据传输对象 (AdminDTO)
- 用户数据访问层 (userMapper)
- 管理员数据访问层 (employeeMapper)
- 参数验证工具 (CommonValidate)
- API规范文档

### 需要移除的字段
- User实体类中的email字段
- UserDTO类中的email字段
- Admin实体类中的email字段
- AdminDTO类中的email字段
- 相关数据库操作中的email参数和引用
- 邮件验证逻辑

## 技术实现细节

### 实体类变更
- 从User类中删除email字段及其getter/setter方法
- 从Admin类中删除email字段及其getter/setter方法

### DTO类变更
- 从UserDTO类中删除email字段及其getter/setter方法
- 从AdminDTO类中删除email字段及其getter/setter方法

### 数据访问层变更
- 修改userMapper中的SQL语句，移除对email字段的所有引用
- 修改employeeMapper中的SQL语句，移除对email字段的所有引用

### 业务逻辑变更
- 修改CommonValidate类中的验证逻辑，移除对email字段的验证
- 更新相关服务类以适应字段变化

## 数据库影响

此变更假定数据库中的email字段将通过数据库迁移脚本移除，本变更不直接处理数据库迁移。

## API变更

- 移除API接口中对email字段的接收和返回
- 更新API文档以反映字段移除

## 向后兼容性

此变更是破坏性的，会影响依赖email字段的客户端应用。需要同步通知所有API使用者。