# 用户服务中移除邮件相关字段的API规范

## 实体定义

### User (用户实体)

| 字段名 | 类型 | 描述 | 是否必填 |
|--------|------|------|----------|
| id | Long | 用户ID | 否(注册时无需提供) |
| username | String | 用户名 | 是 |
| password | String | 密码 | 是 |
| realName | String | 真实姓名 | 否 |
| phone | String | 手机号 | 是 |
| status | String | 状态：1-正常，0-禁用，-1-删除 | 否(注册时默认为"1") |
| createTime | Date | 创建时间 | 否(系统自动生成) |
| updateTime | Date | 更新时间 | 否(系统自动生成) |
| createUser | String | 创建者 | 否(注册时设置为用户名) |
| updateUser | String | 更新者 | 否(系统自动生成) |

### Admin (管理员实体)

| 字段名 | 类型 | 描述 | 是否必填 |
|--------|------|------|----------|
| id | Long | 管理员ID | 否(新增时无需提供) |
| username | String | 用户名 | 是 |
| password | String | 密码 | 是 |
| realName | String | 真实姓名 | 否 |
| role | String | 角色：1-超级管理员，0-普通管理员 | 是 |
| phone | String | 手机号 | 是 |
| status | String | 状态：1-正常，0-禁用，-1-删除 | 否(默认为"1") |
| createTime | Date | 创建时间 | 否(系统自动生成) |
| updateTime | Date | 更新时间 | 否(系统自动生成) |
| createUser | String | 创建者 | 否(系统自动生成) |
| updateUser | String | 更新者 | 否(系统自动生成) |

## API接口定义

### 1. 用户注册
- **接口路径**: POST /register
- **请求参数**: User对象（不含id、status、createTime、updateTime、createUser、updateUser字段）
- **返回结果**: ResultInfo
- **描述**: 允许普通用户自主注册账户
- **注意事项**: 
  - 用户名和手机号必须唯一
  - 注册成功后账户状态默认为"1"（正常）
  - 密码需按系统要求进行加密存储
  - createUser字段设置为注册的用户名

#### 请求示例
```
POST /register
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "realName": "张三",
  "phone": "13800138000"
}
```

#### 响应示例
成功响应：
```
{
  "code": "1",
  "msg": null,
  "data": null
}
```

失败响应：
```
{
  "code": "0",
  "msg": "用户名已存在",
  "data": null
}
```

## 异常处理
- 所有接口应按照统一异常处理机制返回ResultInfo格式的错误信息
- 业务异常需抛出CommonException，系统异常需记录日志并返回通用错误信息
- 常见异常情况：
  - 用户名已存在
  - 手机号已存在
  - 用户名或密码为空
  - 用户名或密码不符合格式要求

## 数据验证
- 用户名、密码、手机号等字段必须进行非空验证
- 用户名长度应在4-20个字符之间
- 密码长度应不少于6个字符
- 手机号应符合手机号格式规范
- 用户名和手机号必须唯一，不能与现有用户重复
- 用户注册后状态默认设置为"1"（正常状态）