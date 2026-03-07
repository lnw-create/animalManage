# 加入志愿活动功能 - 快速参考

## 📋 功能概述
实现了用户加入志愿活动的完整功能，包括参数校验、业务验证、数据记录等。

## 🗂️ 变更文件清单

### 新增文件 (4 个)
```
1. openspec/volunteer-activity-join-spec.md                                    # 功能规范文档
2. openspec/volunteer-activity-join-implementation-summary.md                  # 实现总结文档
3. volunteerActivity-service/src/main/java/.../model/pojo/ActivityParticipant.java      # 参与者实体
4. volunteerActivity-service/src/main/resources/sql/activity_participant.sql            # 数据库迁移脚本
```

### 修改文件 (3 个)
```
1. volunteerActivity-service/src/main/java/.../constant/VolunteerActivityCommonConstant.java  # 添加状态常量
2. volunteerActivity-service/src/main/java/.../mapper/VolunteerActivityMapper.java           # 添加 DB 操作
3. volunteerActivity-service/src/main/java/.../service/impl/VolunteerActivityServiceImpl.java # 实现业务逻辑
```

## 🔧 核心代码位置

### Service 实现
**文件**: `VolunteerActivityServiceImpl.java:138-193`

主要逻辑流程：
```java
1. 参数校验 (id 不能为空)
2. 获取用户 ID (UserContext.getUserId())
3. 查询活动是否存在
4. 验证活动状态 (必须为报名中 "1")
5. 检查是否满员 (currentParticipants < maxParticipants)
6. 防止重复报名 (查询 activity_participant 表)
7. 插入参与者记录
8. 更新活动参与人数 (+1)
```

### Mapper 方法
**文件**: `VolunteerActivityMapper.java:63-87`

新增方法：
- `incrementParticipantCount()` - 增加参与人数（带条件更新）
- `queryParticipantByActivityAndUser()` - 查询是否已报名
- `insertParticipant()` - 插入参与者记录

### 数据库表
**文件**: `activity_participant.sql`

关键字段：
- `activity_id` + `user_id` 唯一索引 (防止重复报名)
- `status` - 参与者状态
- `join_time` - 加入时间

## 🎯 API 接口

```http
POST /volunteerActivity/normalVolunteer/joinActivity
Content-Type: application/x-www-form-urlencoded

请求参数:
id: Long (必填) - 志愿活动 ID

权限要求: normalVolunteer (普通志愿者)
```

## ⚠️ 错误处理

| 错误信息 | 说明 |
|---------|------|
| 志愿活动 id 不能为空 | ID 为 null 或 <= 0 |
| 用户未登录 | UserContext.getUserId() 返回 null |
| 志愿活动不存在 | 活动 ID 对应的记录不存在 |
| 活动不在报名中 | 活动状态不是"1"(报名中) |
| 活动已满员 | currentParticipants >= maxParticipants |
| 您已参加过该活动 | 重复报名检测 |
| 加入活动失败，请稍后重试 | 更新人数失败 |

## 📦 部署步骤

### 1. 执行数据库迁移
```sql
-- 在数据库中执行
source volunteerActivity-service/src/main/resources/sql/activity_participant.sql;
```

### 2. 编译打包
```bash
mvn clean install -DskipTests
```

### 3. 重启服务
```bash
# 重启 volunteerActivity-service 模块
```

### 4. 验证功能
```bash
# 使用 Postman 或 curl 测试
curl -X POST http://localhost:port/volunteerActivity/normalVolunteer/joinActivity?id=1
```

## 🧪 测试用例

### 正常场景
- ✅ 用户成功报名参加志愿活动

### 异常场景
- ❌ 活动 ID 为空
- ❌ 活动不存在
- ❌ 活动状态不是报名中
- ❌ 活动已满员
- ❌ 用户重复报名
- ❌ 用户未登录

## 🔍 关键设计点

### 1. 防重复报名
- **应用层**: 先查询是否已报名
- **数据库层**: 唯一索引 `uk_activity_user(activity_id, user_id)`

### 2. 防超员
- **应用层**: 检查 `currentParticipants < maxParticipants`
- **数据库层**: SQL 条件 `current_participants < max_participants`

### 3. 事务一致性
- 插入参与者记录和更新人数在同一事务中
- 任一操作失败都会回滚

## 📊 数据流程图

```
用户请求
  ↓
参数校验 → 失败 → 返回错误
  ↓
获取用户 ID → 失败 → 返回错误
  ↓
查询活动 → 不存在 → 返回错误
  ↓
检查状态 → 非报名中 → 返回错误
  ↓
检查容量 → 已满员 → 返回错误
  ↓
检查重复 → 已报名 → 返回错误
  ↓
插入参与记录 → 失败 → 回滚并返回错误
  ↓
更新活动人数 → 失败 → 回滚并返回错误
  ↓
成功返回
```

## 🚀 扩展建议

### 短期扩展
1. 取消报名功能
2. 查看已报名用户列表
3. 查看我报名的活动

### 长期扩展
1. 活动签到功能
2. 志愿时长自动计算
3. 活动评价系统
4. 黑名单机制

## 📝 注意事项

1. ⚠️ 必须先执行 SQL 脚本创建表
2. ⚠️ 确保 UserContext 能正确获取用户 ID
3. ⚠️ 生产环境注意并发控制
4. ⚠️ 监控日志中的异常信息

## 🔗 相关文档

- 详细规范：`openspec/volunteer-activity-join-spec.md`
- 实现总结：`openspec/volunteer-activity-join-implementation-summary.md`
