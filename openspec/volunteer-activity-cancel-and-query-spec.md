# 取消志愿活动与查询我的活动功能实现方案

## 1. 概述

本文档描述如何实现两个新功能：
1. **取消志愿活动**：用户取消已报名的志愿活动
2. **查询我的活动**：查询当前用户报名的所有志愿活动

## 2. 需求分析

### 2.1 取消志愿活动

#### 功能需求
- 用户可以取消已报名的志愿活动
- 系统需要验证活动是否存在
- 系统需要验证用户是否已报名该活动
- 取消成功后更新活动的当前参与人数（-1）
- 更新参与者记录状态为"取消"

#### 业务规则
1. 只能取消已报名的活动
2. 取消后活动参与人数减 1
3. 参与者记录状态更新为"0"（取消）
4. 活动必须处于可取消状态（报名中或进行中）

### 2.2 查询我的活动

#### 功能需求
- 查询当前登录用户报名的所有志愿活动
- 支持分页查询
- 支持按活动状态筛选
- 返回活动详细信息及用户的参与状态

#### 业务规则
1. 只返回当前用户参与的志愿活动
2. 支持分页和状态筛选
3. 返回活动信息和参与时间等详情

## 3. 数据库设计

### 3.1 现有表结构
`activity_participant` 表已包含：
- `id`: 主键
- `activity_id`: 志愿活动 ID
- `user_id`: 用户 ID
- `join_time`: 加入时间
- `status`: 状态（1-正常，0-取消，-1-删除）
- `create_time`, `update_time`, `create_user`, `update_user`

### 3.2 需要的查询
```sql
-- 查询用户已报名的活动
SELECT ap.*, a.activity_name, a.description, a.start_time, a.end_time, 
       a.location, a.max_participants, a.current_participants, 
       a.volunteer_hours, a.status as activity_status
FROM activity_participant ap
JOIN activity a ON ap.activity_id = a.id
WHERE ap.user_id = #{userId} 
  AND ap.status = '1'
  AND a.status != '-1'
ORDER BY ap.join_time DESC;
```

## 4. 实现方案

### 4.1 Mapper 层

#### 4.1.1 新增 VolunteerActivityMapper 方法

在 `VolunteerActivityMapper.java` 中添加：

```java
/**
 * 减少活动参与人数
 * @param id 活动 id
 * @param updateUser 更新人
 * @return 影响的行数
 */
@Update("update activity set current_participants = current_participants - 1, update_user = #{updateUser}, update_time = now() where id = #{id} and current_participants > 0")
int decrementParticipantCount(Long id, String updateUser);

/**
 * 根据 ID 查询参与者记录
 * @param id 参与者记录 ID
 * @return 参与者信息
 */
@Select("select * from activity_participant where id = #{id} and status != '-1'")
ActivityParticipant queryParticipantById(Long id);

/**
 * 根据活动和用户查询参与者记录（包含已取消的）
 * @param activityId 活动 ID
 * @param userId 用户 ID
 * @return 参与者信息
 */
@Select("select * from activity_participant where activity_id = #{activityId} and user_id = #{userId}")
ActivityParticipant queryParticipantByActivityAndUserAll(Long activityId, Long userId);

/**
 * 更新参与者状态
 * @param id 参与者记录 ID
 * @param status 新状态
 * @param updateUser 更新人
 * @return 影响的行数
 */
@Update("update activity_participant set status = #{status}, update_user = #{updateUser}, update_time = now() where id = #{id}")
int updateParticipantStatus(Long id, String status, String updateUser);

/**
 * 查询用户参与的活动列表
 * @param userId 用户 ID
 * @param status 活动状态（可选）
 * @return 志愿者活动列表
 */
List<VolunteerActivity> queryMyActivities(@Param("userId") Long userId, @Param("status") String status);
```

### 4.1.2 新增 DTO 类

创建 `com.hutb.volunteerActivity.model.DTO.MyActivityDTO` 类：

```java
package com.hutb.volunteerActivity.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 我的志愿活动 DTO（包含参与信息）
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyActivityDTO {
    // 参与者记录 ID
    private Long participantId;
    
    // 志愿活动 ID
    private Long activityId;
    
    // 活动名称
    private String activityName;
    
    // 活动描述
    private String description;
    
    // 开始时间
    private Date startTime;
    
    // 结束时间
    private Date endTime;
    
    // 活动地点
    private String location;
    
    // 最大参与人数
    private Integer maxParticipants;
    
    // 当前参与人数
    private Integer currentParticipants;
    
    // 志愿时长
    private Double volunteerHours;
    
    // 活动状态
    private String activityStatus;
    
    // 参与状态
    private String participantStatus;
    
    // 加入时间
    private Date joinTime;
}
```

### 4.2 Service 层

#### 4.2.1 更新 VolunteerActivityService 接口

在 `VolunteerActivityService.java` 中添加：

```java
/**
 * 取消志愿活动
 * @param id 志愿活动 ID
 */
void cancelActivity(Long id);

/**
 * 查询当前用户的所有志愿活动
 * @param pageQueryListDTO 分页查询参数
 * @return 我的活动列表
 */
PageInfo queryMyActivities(PageQueryListDTO pageQueryListDTO);
```

#### 4.2.2 实现 VolunteerActivityServiceImpl

在 `VolunteerActivityServiceImpl.java` 中添加：

```java
/**
 * 取消志愿活动
 * @param id 志愿活动 id
 */
@Override
public void cancelActivity(Long id) {
    log.info("取消志愿活动:id-{}", id);
    
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
    
    // 4. 查询用户是否已报名（包含已取消的记录）
    ActivityParticipant participant = volunteerActivityMapper.queryParticipantByActivityAndUserAll(id, userId);
    if (participant == null) {
        throw new CommonException("您未参加过该活动");
    }
    
    // 5. 检查是否已经取消
    if (VolunteerActivityCommonConstant.PARTICIPANT_STATUS_CANCELLED.equals(participant.getStatus())) {
        throw new CommonException("您已取消过该活动");
    }
    
    // 6. 检查活动状态（只能是报名中或进行中的活动可以取消）
    String activityStatus = activity.getStatus();
    if (!VolunteerActivityCommonConstant.ACTIVITY_STATUS_ENROLLING.equals(activityStatus) 
        && !VolunteerActivityCommonConstant.ACTIVITY_STATUS_IN_PROGRESS.equals(activityStatus)) {
        throw new CommonException("当前活动状态不允许取消");
    }
    
    // 7. 更新参与者状态为取消
    int updatedParticipant = volunteerActivityMapper.updateParticipantStatus(
        participant.getId(), 
        VolunteerActivityCommonConstant.PARTICIPANT_STATUS_CANCELLED, 
        UserContext.getUsername()
    );
    if (updatedParticipant == 0) {
        throw new CommonException("取消活动失败，请稍后重试");
    }
    
    // 8. 减少活动参与人数
    int updatedCount = volunteerActivityMapper.decrementParticipantCount(id, UserContext.getUsername());
    if (updatedCount == 0) {
        throw new CommonException("取消活动失败，请稍后重试");
    }
    
    log.info("取消志愿活动成功：活动 id={}, 用户 id={}", id, userId);
}

/**
 * 查询当前用户的所有志愿活动
 * @param pageQueryListDTO 分页查询参数
 * @return 我的活动列表
 */
@Override
public PageInfo queryMyActivities(PageQueryListDTO pageQueryListDTO) {
    log.info("查询当前用户的志愿活动列表:{}", pageQueryListDTO);
    
    // 1. 获取当前用户 ID
    Long userId = UserContext.getUserId();
    if (userId == null) {
        throw new CommonException("用户未登录");
    }
    
    // 2. 分页查询
    Page<Object> page = PageHelper.startPage(pageQueryListDTO.getPageNum(), pageQueryListDTO.getPageSize());
    List<VolunteerActivity> activities = volunteerActivityMapper.queryMyActivities(userId, pageQueryListDTO.getStatus());
    com.github.pagehelper.PageInfo<VolunteerActivity> pageInfo = new com.github.pagehelper.PageInfo<>(activities);
    
    log.info("查询当前用户的志愿活动列表成功，用户 id:{}, 总数:{}", userId, pageInfo.getTotal());
    return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
}
```

### 4.3 Controller 层

在 `VolunteerActivityController.java` 中添加：

```java
/**
 * 取消志愿活动
 */
@PostMapping("normalVolunteer/cancelActivity")
public ResultInfo cancelActivity(@RequestParam Long id) {
    try {
        volunteerActivityService.cancelActivity(id);
        return ResultInfo.success();
    } catch (CommonException e) {
        return ResultInfo.fail(e.getMessage());
    } catch (Exception e) {
        return ResultInfo.fail("系统错误：" + e.getMessage());
    }
}

/**
 * 查询我的志愿活动
 */
@PostMapping("normalVolunteer/myActivities")
public ResultInfo queryMyActivities(@RequestBody PageQueryListDTO pageQueryListDTO) {
    try {
        return ResultInfo.success(volunteerActivityService.queryMyActivities(pageQueryListDTO));
    } catch (CommonException e) {
        return ResultInfo.fail(e.getMessage());
    } catch (Exception e) {
        return ResultInfo.fail("系统错误：" + e.getMessage());
    }
}
```

### 4.4 XML 映射文件

在 `VolunteerActivityMapper.xml` 中添加：

```xml
<!-- 查询用户参与的活动列表 -->
<select id="queryMyActivities" resultType="com.hutb.volunteerActivity.model.pojo.VolunteerActivity">
    SELECT a.*
    FROM activity a
    INNER JOIN activity_participant ap ON a.id = ap.activity_id
    WHERE ap.user_id = #{userId}
      AND ap.status = '1'
      AND a.status != '-1'
    <if test="status != null and status != ''">
        AND a.status = #{status}
    </if>
    ORDER BY a.update_time DESC
</select>
```

## 5. API 接口

### 5.1 取消志愿活动

#### 接口定义
- **URL**: `/volunteerActivity/normalVolunteer/cancelActivity`
- **Method**: POST
- **Request Type**: application/x-www-form-urlencoded
- **权限**: normalVolunteer（普通志愿者）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 志愿活动 ID |

#### 响应示例

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
  "message": "您未参加过该活动",
  "data": null
}
```

### 5.2 查询我的活动

#### 接口定义
- **URL**: `/volunteerActivity/normalVolunteer/myActivities`
- **Method**: POST
- **Request Type**: application/json
- **权限**: normalVolunteer（普通志愿者）

#### 请求参数
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "status": "1"  // 可选，活动状态筛选
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "data": [
      {
        "id": 1,
        "activityName": "关爱流浪动物",
        "description": "帮助收容所照顾流浪动物",
        "startTime": "2024-03-15 09:00:00",
        "endTime": "2024-03-15 17:00:00",
        "location": "市流浪动物收容所",
        "maxParticipants": 20,
        "currentParticipants": 15,
        "volunteerHours": 8.0,
        "status": "1"
      }
    ]
  }
}
```

## 6. 异常处理

### 6.1 取消志愿活动可能抛出的异常
- `CommonException("志愿活动 id 不能为空")`: ID 参数无效
- `CommonException("用户未登录")`: 用户未登录
- `CommonException("志愿活动不存在")`: 活动不存在
- `CommonException("您未参加过该活动")`: 用户未报名该活动
- `CommonException("您已取消过该活动")`: 重复取消
- `CommonException("当前活动状态不允许取消")`: 活动状态不支持取消
- `CommonException("取消活动失败，请稍后重试")`: 更新失败

### 6.2 查询我的活动可能抛出的异常
- `CommonException("用户未登录")`: 用户未登录

## 7. 测试用例

### 7.1 取消志愿活动

#### 正常场景
- ✅ 用户成功取消已报名的活动
- ✅ 验证参与人数正确减少
- ✅ 验证参与者记录状态正确更新

#### 异常场景
- ❌ 活动 ID 为空
- ❌ 用户未登录
- ❌ 活动不存在
- ❌ 用户未报名该活动
- ❌ 用户已取消过该活动
- ❌ 活动已结束，不允许取消

### 7.2 查询我的活动

#### 正常场景
- ✅ 查询用户已报名的所有活动
- ✅ 分页查询正常工作
- ✅ 按状态筛选正常工作

#### 异常场景
- ❌ 用户未登录

## 8. 注意事项

1. 取消活动时需要同时更新参与者记录和活动参与人数
2. 需要考虑事务管理，确保数据一致性
3. 已取消的用户可以重新报名（如果需要此功能）
4. 查询我的活动时只返回正常参与的记录
5. 注意活动状态的判断，已结束的活动不允许取消

## 9. 扩展功能（可选）

### 9.1 重新报名
允许用户重新报名已取消的活动。

### 9.2 取消历史记录
记录用户的取消历史，包括取消时间和原因。

### 9.3 批量取消
管理员批量取消某个活动的所有报名。

### 9.4 取消原因
用户取消时填写取消原因，便于统计分析。
