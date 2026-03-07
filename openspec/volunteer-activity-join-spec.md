# 加入志愿活动功能实现方案

## 1. 概述

本文档描述如何实现用户加入志愿活动的功能。该功能允许志愿者报名参加志愿活动，并自动更新活动的当前参与人数。

## 2. 需求分析

### 2.1 功能需求
- 用户可以通过活动 ID 报名加入志愿活动
- 系统需要验证活动是否存在
- 系统需要检查活动是否处于可报名状态
- 系统需要检查活动是否已满员
- 系统需要防止重复报名
- 成功后更新活动的当前参与人数

### 2.2 业务规则
1. 只有状态为"报名中"（status = '1'）的活动才能加入
2. 当当前参与人数达到最大参与人数时，不允许继续报名
3. 同一用户不能重复加入同一个志愿活动
4. 加入成功后，活动的当前参与人数 +1

## 3. 数据库设计

### 3.1 现有表结构
志愿活动表 `activity` 已包含以下相关字段：
- `id`: 主键
- `activity_name`: 活动名称
- `max_participants`: 最大参与人数
- `current_participants`: 当前参与人数
- `status`: 活动状态（1-报名中，2-进行中，3-已结束，-1-已删除）

### 3.2 新增表 - 活动参与者表
为了记录用户参与情况和防止重复报名，需要创建参与者关联表：

```sql
CREATE TABLE `activity_participant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `activity_id` bigint(20) NOT NULL COMMENT '志愿活动 ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `status` varchar(10) DEFAULT '1' COMMENT '状态：1-正常，0-取消，-1-删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`) COMMENT '防止重复报名',
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿活动参与者表';
```

## 4. 实现方案

### 4.1 Mapper 层

#### 4.1.1 新增 VolunteerActivityMapper 方法

在 `VolunteerActivityMapper.java` 中添加：

```java
/**
 * 增加活动参与人数
 * @param id 活动 id
 * @return 影响的行数
 */
@Update("update activity set current_participants = current_participants + 1, update_user = #{updateUser}, update_time = now() where id = #{id} and status = '1' and current_participants < max_participants")
int incrementParticipantCount(Long id, String updateUser);

/**
 * 查询用户是否已参与活动
 * @param activityId 活动 id
 * @param userId 用户 id
 * @return 参与记录
 */
@Select("select * from activity_participant where activity_id = #{activityId} and user_id = #{userId} and status = '1'")
ActivityParticipant queryParticipantByActivityAndUser(Long activityId, Long userId);

/**
 * 添加参与者记录
 * @param participant 参与者信息
 */
@Insert("insert into activity_participant(activity_id, user_id, join_time, status, create_time, update_time, create_user, update_user) " +
        "values(#{activityId}, #{userId}, #{joinTime}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
void insertParticipant(ActivityParticipant participant);
```

#### 4.1.2 新增 ActivityParticipant 实体类

创建 `com.hutb.volunteerActivity.model.pojo.ActivityParticipant` 类：

```java
package com.hutb.volunteerActivity.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 志愿活动参与者
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivityParticipant {
    private Long id;
    
    private Long activityId;
    
    private Long userId;
    
    private Date joinTime;
    
    // 状态：1-正常 0-取消 -1-删除
    private String status = "1";
    
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
}
```

### 4.2 Service 层

#### 4.2.1 更新 VolunteerActivityService 接口

接口中已有 `joinActivity` 方法定义，无需修改。

#### 4.2.2 实现 VolunteerActivityServiceImpl.joinActivity

在 `VolunteerActivityServiceImpl.java` 中实现该方法：

```java
/**
 * 加入志愿活动
 * @param id 志愿活动 id
 */
@Override
public void joinActivity(Long id) {
    log.info("加入志愿活动:id-{}", id);
    
    // 1. 参数校验
    if (id == null || id <= 0) {
        throw new CommonException("志愿活动 id 不能为空");
    }
    
    // 2. 获取当前用户 ID
    Long userId = UserContext.getUserId();
    if (userId == null) {
        throw new CommonException("用户未登录");
    }
    
    // 3. 查询志愿活动是否存在
    VolunteerActivity activity = volunteerActivityMapper.queryVolunteerActivityById(id, VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
    if (activity == null) {
        throw new CommonException("志愿活动不存在");
    }
    
    // 4. 检查活动状态是否为报名中
    if (!VolunteerActivityCommonConstant.ACTIVITY_STATUS_ENROLLING.equals(activity.getStatus())) {
        throw new CommonException("活动不在报名中");
    }
    
    // 5. 检查是否已满员
    if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
        throw new CommonException("活动已满员");
    }
    
    // 6. 检查用户是否已报名
    ActivityParticipant existingParticipant = volunteerActivityMapper.queryParticipantByActivityAndUser(id, userId);
    if (existingParticipant != null) {
        throw new CommonException("您已参加过该活动");
    }
    
    // 7. 添加参与者记录
    ActivityParticipant participant = new ActivityParticipant();
    participant.setActivityId(id);
    participant.setUserId(userId);
    participant.setJoinTime(new Date());
    participant.setStatus(VolunteerActivityCommonConstant.ACTIVITY_STATUS_ENROLLING);
    participant.setCreateTime(new Date());
    participant.setUpdateTime(new Date());
    participant.setCreateUser(UserContext.getUsername());
    participant.setUpdateUser(UserContext.getUsername());
    volunteerActivityMapper.insertParticipant(participant);
    
    // 8. 更新活动参与人数
    int updated = volunteerActivityMapper.incrementParticipantCount(id, UserContext.getUsername());
    if (updated == 0) {
        throw new CommonException("加入活动失败，请稍后重试");
    }
    
    log.info("加入志愿活动成功：活动 id={}, 用户 id={}", id, userId);
}
```

### 4.3 Controller 层

Controller 层已有接口定义，无需修改：

```java
/**
 * 参加志愿活动
 */
@PostMapping("normalVolunteer/joinActivity")
public ResultInfo joinActivity(@RequestParam Long id) {
    try {
        volunteerActivityService.joinActivity(id);
        return ResultInfo.success();
    } catch (CommonException e) {
        return ResultInfo.fail(e.getMessage());
    } catch (Exception e) {
        return ResultInfo.fail("系统错误：" + e.getMessage());
    }
}
```

### 4.4 XML 映射文件

在 `VolunteerActivityMapper.xml` 中添加（可选，如果使用注解则不需要）：

```xml
<!-- 如果需要使用 XML 配置复杂 SQL，可以在此添加 -->
```

## 5. 常量定义

在 `VolunteerActivityCommonConstant` 中添加参与者状态常量：

```java
/**
 * 参与者状态
 */
// 正常参与
public static final String PARTICIPANT_STATUS_NORMAL = "1";

// 取消参与
public static final String PARTICIPANT_STATUS_CANCELLED = "0";

// 已删除
public static final String PARTICIPANT_STATUS_DELETED = "-1";
```

## 6. 异常处理

可能抛出的异常：
- `CommonException("志愿活动 id 不能为空")`: ID 参数无效
- `CommonException("用户未登录")`: 用户未登录
- `CommonException("志愿活动不存在")`: 活动不存在
- `CommonException("活动不在报名中")`: 活动状态不允许报名
- `CommonException("活动已满员")`: 报名人数已达上限
- `CommonException("您已参加过该活动")`: 重复报名
- `CommonException("加入活动失败，请稍后重试")`: 更新人数失败

## 7. API 接口

### 7.1 接口定义
- **URL**: `/volunteerActivity/normalVolunteer/joinActivity`
- **Method**: POST
- **Request Type**: application/x-www-form-urlencoded / multipart/form-data
- **权限**: normalVolunteer（普通志愿者）

### 7.2 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 志愿活动 ID |

### 7.3 响应示例

成功响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

失败响应：
```json
{
  "code": 400,
  "message": "活动已满员",
  "data": null
}
```

## 8. 测试用例

### 8.1 正常场景
- 用户成功报名参加志愿活动
- 验证参与人数正确增加
- 验证参与者记录正确创建

### 8.2 异常场景
- 活动不存在
- 活动不在报名中
- 活动已满员
- 用户重复报名
- 用户未登录
- ID 参数为空或负数

## 9. 注意事项

1. 使用唯一索引 `uk_activity_user` 防止并发情况下的重复报名
2. 更新参与人数时使用条件判断，确保不会超过最大人数限制
3. 需要考虑事务管理，确保参与者记录和人数更新的原子性
4. 后续可扩展取消报名、查看已报名用户列表等功能

## 10. 扩展功能（可选）

### 10.1 取消报名
提供接口允许用户取消已报名的活动。

### 10.2 查看报名列表
提供接口供管理员查看某个活动的所有报名用户。

### 10.3 查看我报名的活动
提供接口供用户查看自己报名的所有活动。
