# 网关层权限校验功能SPEC变更

## 1. 变更概述

### 1.1 变更目的
为系统网关层增加统一的权限校验功能，实现基于用户角色的访问控制，确保不同角色用户只能访问其被授权的资源。

### 1.2 变更背景
当前系统网关层仅实现了JWT身份验证，但缺乏细粒度的权限控制机制。为提升系统安全性，需要根据用户角色实现差异化访问控制。

### 1.3 影响范围
- 网关层（gateway）的全局过滤器
- 用户服务（user-service）中的用户角色定义
- 各业务微服务的访问控制逻辑

## 2. 功能需求

### 2.1 权限角色定义
系统定义以下四种用户角色，具体常量值位于 `com/hutb/user/constant/UserCommonConstant.java`：

- **超管** (`super_admin`): 系统最高权限用户，拥有全部操作权限
- **普通管理员** (`normal_admin`): 可管理自身、普通用户及志愿者
- **普通用户** (`normal_user`): 仅可管理自己的资源
- **志愿者** (`volunteer`): 可参与志愿活动服务及商品服务

### 2.2 权限控制规则

#### 2.2.1 超管权限
- 可进行所有操作
- 访问所有微服务的所有接口
- 不受任何权限限制

#### 2.2.2 普通管理员权限
- 可管理自己的账户信息
- 可管理普通用户和志愿者的信息
- 可访问相关业务接口

#### 2.2.3 普通用户权限
- 仅可管理自己的信息
- 仅可查看志愿者服务内容（只读权限）
- 无法进行具体的业务操作

#### 2.2.4 志愿者权限
- 可参与志愿活动服务
- 可使用商品服务
- 在志愿者相关功能中具有完整的CRUD权限

### 2.3 特殊权限关系说明
根据实体类分析：
- 用户表([User](file:///C:/Users/27932/Desktop/code/animalManage/user-service/src/main/java/com/hutb/user/model/pojo/User.java))中通过role字段区分普通用户(`normal_user`)和志愿者(`volunteer`)
- 志愿者表([Volunteer](file:///C:/Users/27932/Desktop/code/animalManage/user-service/src/main/java/com/hutb/user/model/pojo/Volunteer.java))通过userId关联到用户表
- 普通用户角色的用户可以查看志愿者服务内容，但不能进行具体业务操作
- 志愿者角色的用户可以实际参与志愿活动和使用商品服务

## 3. 技术实现方案

### 3.1 网关层权限过滤器
在现有的[GlobalFilter](file:///C:/Users/27932/Desktop/code/animalManage/gateway/src/main/java/com/hutb/gateway/filter/GlobalFilter.java)基础上扩展权限校验逻辑：

1. 验证JWT令牌的有效性（已实现）
2. 解析用户角色信息
3. 根据请求路径和用户角色判断是否有访问权限
4. 对于无权限的请求返回403错误

### 3.2 权限映射配置
建议在网关层维护一个权限映射配置，定义各角色对不同服务接口的访问权限：

```yaml
permissions:
  # 超管权限 - 所有访问
  super_admin: 
    - "/**"
  
  # 普通管理员权限
  normal_admin:
    - "/user/**" # 可管理用户
    - "/volunteer/**" # 可管理志愿者
    - "/**" # 但不能访问特定的管理功能
  
  # 普通用户权限
  normal_user:
    - "/user/self/**" # 只能管理自己
    - "/volunteer/activity/view/**" # 只读查看志愿活动
  
  # 志愿者权限
  volunteer:
    - "/volunteer/activity/**" # 志愿活动相关
    - "/shopping/**" # 商品服务相关
```

### 3.3 实现细节
1. 扩展JWT令牌，包含用户角色信息
2. 在[GlobalFilter](file:///C:/Users/27932/Desktop/code/animalManage/gateway/src/main/java/com/hutb/gateway/filter/GlobalFilter.java)中解析角色信息
3. 基于路径匹配和角色验证决定是否放行请求
4. 对于需要特殊权限检查的复杂场景，可以在目标微服务中进一步校验

## 4. 安全考虑
- 所有权限校验应在网关层完成，避免绕过权限控制直接访问微服务
- 对于涉及敏感数据的操作，微服务端也应进行二次权限校验
- 日志记录权限拒绝事件，便于安全审计

## 5. 测试要点
- 验证各角色用户的访问权限是否符合预期
- 测试边界情况（如角色信息缺失或损坏的JWT令牌）
- 验证权限校验不会影响系统性能