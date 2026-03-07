# 志愿活动参与者状态管理 - 简化方案

## 🎯 核心设计理念

### 两种状态设计
**只存在正常和删除两种状态：**
- ✅ `status='1'` - **正常参与**
- ✅ `status='-1'` - **已删除**（用户取消活动）
- ❌ ~~`status='0'`~~ - **废弃不用**（已取消状态）

### 核心思路
**重新报名时恢复原有记录的状态，而不是创建新记录：**
```
首次报名 → 创建记录 (status='1')
   ↓
取消活动 → 更新 status='-1'（逻辑删除，记录仍存在）
   ↓
重新报名 → 恢复 status='1'（不创建新记录）✅
```

---

## 🔧 实现方案

### 1. joinActivity() - 加入志愿活动

**文件**: `VolunteerActivityServiceImpl.java:168-215`

**核心逻辑：**
```java
// 6. 查询用户是否有过参与记录（包括已删除的）
ActivityParticipant existingParticipant = volunteerActivityMapper.queryParticipantByActivityAndUserAll(id, userId);
if (existingParticipant != null) {
    if (PARTICIPANT_STATUS_NORMAL.equals(existingParticipant.getStatus())) {
        // 已经是正常参与状态 → 报错
        throw new CommonException("您已参加过该活动");
    } else if (PARTICIPANT_STATUS_DELETED.equals(existingParticipant.getStatus())) {
        // 用户之前删除过，现在重新报名 → 恢复状态为正常
        int updated = volunteerActivityMapper.updateParticipantStatus(
                existingParticipant.getId(),
                PARTICIPANT_STATUS_NORMAL,
                UserContext.getUsername()
        );
        
        // 更新活动参与人数 +1
        volunteerActivityMapper.incrementParticipantCount(id, UserContext.getUsername());
        
        log.info("重新加入志愿活动成功：活动 id={}, 用户 id={}", id, userId);
        return;
    }
}

// 7. 没有历史记录，创建新的参与者记录
ActivityParticipant participant = new ActivityParticipant();
// ... 设置字段
volunteerActivityMapper.insertParticipant(participant);

// 8. 更新活动参与人数 +1
volunteerActivityMapper.incrementParticipantCount(id, UserContext.getUsername());
```

**关键点：**
- ✅ 使用 `queryParticipantByActivityAndUserAll()` 查询所有状态的记录
- ✅ 如果是 `status='1'` → 不允许重复报名
- ✅ 如果是 `status='-1'` → 恢复状态为 '1'，不创建新记录
- ✅ 如果没有记录 → 创建新记录

---

### 2. cancelActivity() - 取消志愿活动

**文件**: `VolunteerActivityServiceImpl.java:222-273`

**核心逻辑：**
```java
// 4. 查询用户是否已报名（只查询正常参与的记录）
ActivityParticipant participant = volunteerActivityMapper.queryParticipantByActivityAndUser(id, userId);
if (participant == null) {
    throw new CommonException("您未参加过该活动");
}

// 5. 检查活动状态（只能是报名中或进行中）
String activityStatus = activity.getStatus();
if (!ACTIVITY_STATUS_ENROLLING.equals(activityStatus) 
    && !ACTIVITY_STATUS_IN_PROGRESS.equals(activityStatus)) {
    throw new CommonException("当前活动状态不允许取消");
}

// 6. 删除参与者记录（设置为已删除状态）
int updatedParticipant = volunteerActivityMapper.updateParticipantStatus(
    participant.getId(), 
    PARTICIPANT_STATUS_DELETED, // '-1'
    UserContext.getUsername()
);

// 7. 减少活动参与人数 -1
volunteerActivityMapper.decrementParticipantCount(id, UserContext.getUsername());
```

**关键点：**
- ✅ 使用 `queryParticipantByActivityAndUser()` 只查询 `status='1'` 的记录
- ✅ 取消时设置 `status='-1'`（逻辑删除）
- ✅ 减少活动参与人数

---

## 📊 数据流程示例

### 完整流程

| 步骤 | 操作 | status | 活动人数 | 数据库记录数 | 说明 |
|------|------|--------|---------|------------|------|
| 1 | 用户首次报名 | '1' | +1 | 1 | INSERT |
| 2 | 用户取消活动 | '-1' | -1 | 1 | UPDATE |
| 3 | 用户重新报名 | '1' | +1 | 1 | **UPDATE** ✅ |
| 4 | 用户再次取消 | '-1' | -1 | 1 | UPDATE |
| 5 | 用户再次报名 | '1' | +1 | 1 | **UPDATE** ✅ |

**关键优势：**
- ✅ 数据库中每个用户对每个活动**只有一条记录**
- ✅ 不会出现唯一索引冲突
- ✅ 保留完整的参与历史
- ✅ 支持多次取消和重新报名

---

## 🛠️ Mapper 方法

### 需要的查询方法

**VolunteerActivityMapper.java:**

```java
/**
 * 查询正常参与的记录（用于防重复检查、取消活动）
 */
@Select("select * from activity_participant where activity_id = #{activityId} and user_id = #{userId} and status = '1'")
ActivityParticipant queryParticipantByActivityAndUser(Long activityId, Long userId);

/**
 * 查询所有状态的记录（用于重新报名）
 */
@Select("select * from activity_participant where activity_id = #{activityId} and user_id = #{userId}")
ActivityParticipant queryParticipantByActivityAndUserAll(Long activityId, Long userId);

/**
 * 更新参与者状态
 */
@Update("update activity_participant set status = #{status}, update_user = #{updateUser}, update_time = now() where id = #{id}")
int updateParticipantStatus(Long id, String status, String updateUser);

/**
 * 增加活动参与人数
 */
@Update("update activity set current_participants = current_participants + 1, update_user = #{updateUser}, update_time = now() " +
        "where id = #{id} and status = '1' and current_participants < max_participants")
int incrementParticipantCount(Long id, String updateUser);

/**
 * 减少活动参与人数
 */
@Update("update activity set current_participants = current_participants - 1, update_user = #{updateUser}, update_time = now() " +
        "where id = #{id} and current_participants > 0")
int decrementParticipantCount(Long id, String updateUser);

/**
 * 插入参与者记录（仅首次报名使用）
 */
@Insert("insert into activity_participant(activity_id, user_id, join_time, status, create_time, update_time, create_user, update_user) " +
        "values(#{activityId}, #{userId}, #{joinTime}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
void insertParticipant(ActivityParticipant participant);
```

---

## 🧪 测试用例

### 场景 1: 首次报名
```bash
curl -X POST .../joinActivity?id=1
# ✅ 成功
# SQL: INSERT INTO activity_participant ...
# 数据库：新增一条记录 (status='1')
```

### 场景 2: 取消活动
```bash
curl -X POST .../cancelActivity?id=1
# ✅ 成功
# SQL: UPDATE activity_participant SET status='-1' ...
# 数据库：同一条记录，status 从 '1' 变为 '-1'
```

### 场景 3: 重新报名（关键测试）✅
```bash
# 前置条件：用户已取消活动（status='-1'）
curl -X POST .../joinActivity?id=1
# ✅ 成功
# SQL: UPDATE activity_participant SET status='1' WHERE id=? ...
# 数据库：同一条记录，status 从 '-1' 恢复为 '1'
# 不会产生新记录！
```

### 场景 4: 重复报名
```bash
# 前置条件：用户已报名（status='1'）
curl -X POST .../joinActivity?id=1
# ❌ "您已参加过该活动"
```

---

## 📈 数据库记录变化示例

**用户 ID=5，活动 ID=1 的完整历史：**

| 时间 | 操作 | id | activity_id | user_id | status | join_time | update_time |
|------|------|----|-------------|---------|--------|-----------|-------------|
| 10:00 | 首次报名 | 1 | 1 | 5 | '1' | 10:00 | 10:00 |
| 10:30 | 取消活动 | 1 | 1 | 5 | '-1' | 10:00 | 10:30 |
| 11:00 | 重新报名 | 1 | 1 | 5 | '1' | 10:00 | 11:00 |
| 11:30 | 再次取消 | 1 | 1 | 5 | '-1' | 10:00 | 11:30 |
| 12:00 | 再次报名 | 1 | 1 | 5 | '1' | 10:00 | 12:00 |

**关键观察：**
- ✅ **始终只有一条记录**（id=1）
- ✅ `join_time` 保持首次报名的时间
- ✅ `status` 在 '1' 和 '-1' 之间切换
- ✅ `update_time` 记录每次状态变更的时间

---

## 🎯 优势对比

### 原方案（错误）❌

| 问题 | 描述 |
|------|------|
| ❌ 多次创建记录 | 每次报名都 INSERT，导致多条记录 |
| ❌ 唯一索引冲突 | 违反 `uk_activity_user` 约束 |
| ❌ 数据冗余 | 同一用户对同一活动有多条记录 |
| ❌ 需要清理逻辑 | 需要定期清理无用记录 |

### 新方案（正确）✅

| 优势 | 描述 |
|------|------|
| ✅ 单一记录 | 每个用户对每个活动只有一条记录 |
| ✅ 无冲突 | 不会违反唯一索引约束 |
| ✅ 数据简洁 | 没有冗余记录 |
| ✅ 保留历史 | 通过 `update_time` 追踪变更历史 |
| ✅ 支持恢复 | 可以多次取消和重新报名 |

---

## 💡 最佳实践总结

### 1. 状态设计原则

✅ **极简主义：**
- 只用两种状态：正常（'1'）、删除（'-1'）
- 避免复杂的状态机

✅ **语义明确：**
- `'1'` = 正常参与
- `'-1'` = 已删除（逻辑删除）

---

### 2. 查询策略

✅ **根据场景选择方法：**

| 场景 | 查询方法 | 查询条件 |
|------|---------|---------|
| 防重复检查 | `queryParticipantByActivityAndUser()` | `status='1'` |
| 取消活动 | `queryParticipantByActivityAndUser()` | `status='1'` |
| 重新报名 | `queryParticipantByActivityAndUserAll()` | 无限制 |
| 查询我的活动 | `queryMyActivities()` | JOIN + `status='1'` |

---

### 3. 操作流程

✅ **加入活动的决策树：**
```
查询用户记录
   ↓
有记录？
├─ 是，status='1' → ❌ "您已参加过该活动"
├─ 是，status='-1' → ✅ 恢复状态为 '1'（UPDATE）
└─ 否 → ✅ 创建新记录（INSERT）
```

✅ **取消活动的流程：**
```
查询用户记录（status='1'）
   ↓
有记录？
├─ 否 → ❌ "您未参加过该活动"
└─ 是 → ✅ 更新状态为 '-1'（UPDATE）
```

---

## 📦 部署说明

### 1. 无需数据库变更
直接使用已有的表结构和唯一索引

### 2. 编译打包
```bash
mvn clean install -DskipTests
```

### 3. 重启服务
```bash
# 重启 volunteerActivity-service
```

### 4. 验证功能
按上述测试用例验证

---

## 🚀 扩展建议

### 未来可能的扩展

#### 1. 黑名单机制
```sql
-- 增加字段
ALTER TABLE activity_participant 
ADD COLUMN ban_reason VARCHAR(500);

-- 管理员可以将用户加入黑名单
UPDATE activity_participant 
SET status = '-1', ban_reason = '恶意刷单'
WHERE activity_id = ? AND user_id = ?;
```

#### 2. 自动恢复机制
```java
// 如果用户取消超过 N 次，自动禁止其报名
if (countCancelledTimes(userId) >= 3) {
    throw new CommonException("您已被禁止参加志愿活动");
}
```

#### 3. 统计分析
```sql
-- 统计用户的参与历史
SELECT 
    user_id,
    COUNT(*) as total_joins,
    SUM(CASE WHEN status='1' THEN 1 ELSE 0 END) as active_count,
    SUM(CASE WHEN status='-1' THEN 1 ELSE 0 END) as cancelled_count
FROM activity_participant
GROUP BY user_id;
```

---

## 📝 注意事项

### 开发注意事项

1. ⚠️ **区分查询方法**：
   - `queryParticipantByActivityAndUser()` - 只查正常参与
   - `queryParticipantByActivityAndUserAll()` - 查所有状态

2. ⚠️ **事务一致性**：
   - 恢复状态和增加人数在同一事务中
   - 任一失败都要回滚

3. ⚠️ **日志记录**：
   - 记录重新报名的操作日志
   - 便于问题排查

### 运维注意事项

1. ⚠️ **监控指标**：
   - 重新报名的成功率
   - 活动参与人数的准确性

2. ⚠️ **数据清理**：
   - 定期清理长期不活跃的用户记录
   - 建议保留至少 90 天的历史数据

---

## 🔗 相关文档

- **原始设计**: `openspec/volunteer-activity-join-spec.md`
- **并发修复**: `openspec/volunteer-activity-join-fix.md`
- **状态策略**: `openspec/volunteer-activity-participant-status-strategy.md`
- **本文档**: `openspec/volunteer-activity-simplified-status-design.md`

---

**修正时间**: 2026-03-07  
**修正状态**: ✅ 完成  
**编译状态**: ✅ 通过  
**测试状态**: 待验证
