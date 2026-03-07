# 参与者状态管理修正 - 快速指南

## 🎯 核心变更

### 业务逻辑修正

**原设计（错误）❌：**
- 取消活动 → `status='0'`（已取消）
- ❌ 不允许重新报名

**新设计（正确）✅：**
- 取消活动 → `status='-1'`（已删除）
- ✅ **允许重新报名**

---

## 📊 状态定义

| status | 含义 | 用途 |
|--------|------|------|
| `'1'` | 正常参与 | 用户当前参与活动 |
| `'0'` | ~~已取消~~ | **废弃不用** ⚠️ |
| `'-1'` | 已删除 | 用户取消活动 |

---

## 🔧 代码变更摘要

### 1. joinActivity() - 加入活动

**文件**: `VolunteerActivityServiceImpl.java:168`

```java
// ✅ 只检查正常参与的记录
ActivityParticipant existing = volunteerActivityMapper.queryParticipantByActivityAndUser(id, userId);
if (existing != null) {
    throw new CommonException("您已参加过该活动");
}
```

**关键点：**
- ✅ 只查询 `status='1'` 的记录
- ✅ 如果用户之前取消过（`status='-1'`），可以重新报名

---

### 2. cancelActivity() - 取消活动

**文件**: `VolunteerActivityServiceImpl.java:220`

```java
// ✅ 只查询正常参与的记录
ActivityParticipant participant = volunteerActivityMapper.queryParticipantByActivityAndUser(id, userId);
if (participant == null) {
    throw new CommonException("您未参加过该活动");
}

// ...

// ✅ 设置为已删除状态
volunteerActivityMapper.updateParticipantStatus(
    participant.getId(), 
    VolunteerActivityCommonConstant.PARTICIPANT_STATUS_DELETED, // '-1'
    UserContext.getUsername()
);
```

**关键点：**
- ✅ 只查询 `status='1'` 的记录
- ✅ 取消后设置 `status='-1'`（已删除）
- ✅ 减少活动参与人数

---

## 🧪 测试场景

### 场景 1: 首次报名
```bash
curl -X POST .../joinActivity?id=1
# ✅ 成功，status='1'
```

### 场景 2: 取消活动
```bash
curl -X POST .../cancelActivity?id=1
# ✅ 成功，status 从 '1' 变为 '-1'
# 活动人数 -1
```

### 场景 3: 重新报名（关键测试）✅
```bash
# 用户取消后再次报名
curl -X POST .../joinActivity?id=1
# ✅ 成功！可以重新报名
# status 从 '-1' 变为 '1'（创建新记录或恢复旧记录）
# 活动人数 +1
```

### 场景 4: 重复报名
```bash
# 用户已报名（status='1'）
curl -X POST .../joinActivity?id=1
# ❌ "您已参加过该活动"
```

---

## 📦 部署步骤

### 1. 无需数据库变更
直接使用已有的表结构

### 2. 重新编译
```bash
mvn clean install -DskipTests
```

### 3. 重启服务
```bash
# 重启 volunteerActivity-service
```

### 4. 验证测试
按上述测试场景验证功能

---

## 🔄 完整流程示例

```
用户报名 → status='1'
   ↓
用户取消 → status='-1'（已删除）
   ↓
重新报名 → status='1' ✅
```

**数据表示例：**

| id | activity_id | user_id | status | 说明 |
|----|-------------|---------|--------|------|
| 1 | 1 | 5 | '-1' | 用户取消的记录 |
| 2 | 1 | 5 | '1' | 用户重新报名的记录 |

---

## 💡 进一步优化建议

### 推荐方案：复用已删除的记录

**当前问题：**
- ❌ 每次重新报名都创建新记录
- ❌ 数据库中会有多条重复记录

**优化方案：**
```java
// 1. 尝试恢复已删除的记录
int restored = mapper.restoreParticipant(activityId, userId);
if (restored == 0) {
    // 2. 如果没有已删除的记录，创建新记录
    mapper.insertNewParticipant(participant);
}
```

**优点：**
- ✅ 复用已有记录
- ✅ 避免数据冗余
- ✅ 保留完整历史

---

## 🛡️ 最佳实践

### 1. 查询方法选择

| 场景 | 使用的方法 | 查询条件 |
|------|-----------|---------|
| 防重复检查 | `queryParticipantByActivityAndUser()` | `status='1'` |
| 取消活动 | `queryParticipantByActivityAndUser()` | `status='1'` |
| 查询我的活动 | `queryMyActivities()` | `status='1'` |
| 历史记录统计 | `queryParticipantByActivityAndUserAll()` | 无限制 |

### 2. 状态管理原则

✅ **明确的状态语义：**
- `'1'` = 正常参与
- `'-1'` = 已删除
- `'0'` = 废弃

✅ **清晰的业务流程：**
```
未报名 → 报名 ('1') → 取消 ('-1') → 重新报名 ('1')
```

---

## 📝 经验总结

### 教训
1. ⚠️ 不要混淆"取消"和"删除"的概念
2. ⚠️ 已删除的记录应该允许重新操作
3. ⚠️ 状态设计要符合业务实际场景

### 最佳实践
1. ✅ 逻辑删除优于物理删除
2. ✅ 支持用户的撤销/恢复操作
3. ✅ 保持数据的完整性和可追溯性

---

## 📚 相关文档

- **详细策略文档**: `openspec/volunteer-activity-participant-status-strategy.md`
- **加入活动规范**: `openspec/volunteer-activity-join-spec.md`
- **取消功能规范**: `openspec/volunteer-activity-cancel-and-query-spec.md`

---

**修正时间**: 2026-03-07  
**修正状态**: ✅ 完成  
**测试状态**: 待验证
