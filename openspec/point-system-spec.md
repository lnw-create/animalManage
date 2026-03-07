# 志愿活动积分系统与购物积分扣减功能实现方案

## 1. 概述

### 1.1 项目背景
随着动物管理系统业务的发展，需要建立完整的积分激励机制。用户参与志愿活动后应获得相应积分奖励，在兑换商品时消耗积分，形成完整的积分闭环。

### 1.2 当前状态
- ✅ 志愿者表中已有 `activity_point` 字段用于存储积分
- ✅ 志愿活动表中有 `volunteer_hours` 字段表示活动时长
- ✅ 订单系统已支持积分支付（`total_integral` 字段）
- ❌ 缺少参与志愿活动后的积分累积逻辑
- ❌ 缺少创建订单时的积分余额校验和扣减逻辑

### 1.3 功能目标
1. **志愿活动积分累积**
   - 用户完成志愿活动后自动获得积分
   - 积分计算基于活动时长
   - 更新志愿者表中的积分余额

2. **购物积分扣减**
   - 创建订单时校验用户积分余额
   - 积分不足时阻止下单
   - 下单成功后扣减相应积分

3. **数据一致性保障**
   - 积分变动使用事务管理
   - 防止积分扣减成负数
   - 确保积分累计和扣减的原子性

---

## 2. 需求分析

### 2.1 功能需求

#### 2.1.1 志愿活动积分累积
- 用户在志愿活动结束后获得积分
- 积分 = 志愿时长 × 积分系数（默认 1 小时=10 积分）
- 支持活动完成时批量发放积分
- 记录积分变动历史（可选扩展）

#### 2.1.2 购物积分扣减
- 创建订单时检查用户积分余额
- 积分余额 ≥ 订单所需积分时才允许下单
- 下单成功同时扣减用户积分
- 订单取消时考虑积分返还（扩展功能）

#### 2.1.3 积分查询
- 用户可查询当前积分余额
- 管理员可查看用户积分详情

### 2.2 业务规则

#### 2.2.1 积分获取规则
1. **基础规则**
   - 完成志愿活动即可获得积分
   - 积分 = 活动志愿时长 × 10
   - 例如：参与 2 小时的活动获得 20 积分

2. **发放时机**
   - 活动状态变更为"已完成"时
   - 或管理员手动确认活动完成后

3. **发放对象**
   - 仅向正常参与活动的用户发放（`activity_participant.status = '1'`）
   - 已取消或被删除的参与记录不发放积分

#### 2.2.2 积分扣减规则
1. **扣减时机**
   - 订单创建成功时立即扣减
   - 订单状态为"pending_payment"时冻结积分（可选扩展）

2. **扣减条件**
   - 用户积分余额必须 ≥ 订单总积分
   - 不允许积分扣减为负数

3. **异常处理**
   - 积分不足时抛出明确的错误提示
   - 扣减失败时回滚订单创建

### 2.3 非功能需求
1. **性能要求**
   - 积分查询响应时间 < 100ms
   - 积分扣减操作响应时间 < 200ms

2. **一致性要求**
   - 积分变动必须使用事务
   - 防止并发场景下的积分超扣

3. **安全性要求**
   - 积分扣减接口需要权限验证
   - 防止恶意刷积分

---

## 3. 数据库设计

### 3.1 现有表结构分析

#### 3.1.1 volunteer 表（已有）
```sql
CREATE TABLE volunteer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    username VARCHAR(255),
    activity_point INT DEFAULT 0 COMMENT '积分余额',
    total_hours DOUBLE DEFAULT 0.0 COMMENT '累计志愿时长',
    activity_count INT DEFAULT 0 COMMENT '参与活动次数',
    status VARCHAR(10) DEFAULT '1',
    -- 其他字段...
    INDEX idx_user_id (user_id)
) COMMENT='志愿者表';
```

**评估**: ✅ 已有 `activity_point` 字段，无需修改

#### 3.1.2 activity 表（已有）
```sql
CREATE TABLE activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_name VARCHAR(255),
    volunteer_hours DOUBLE COMMENT '志愿时长',
    status VARCHAR(20) COMMENT '活动状态',
    -- 其他字段...
) COMMENT='志愿活动表';
```

**评估**: ✅ 已有 `volunteer_hours` 字段，无需修改

#### 3.1.3 orders 表（已有）
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_integral INT COMMENT '订单总积分',
    status VARCHAR(50) COMMENT '订单状态',
    -- 其他字段...
) COMMENT='订单表';
```

**评估**: ✅ 已有 `total_integral` 字段，无需修改

#### 3.1.4 activity_participant 表（已有）
```sql
CREATE TABLE activity_participant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(10) DEFAULT '1' COMMENT '状态：1-正常，0-取消，-1-删除',
    -- 其他字段...
) COMMENT='志愿活动参与者表';
```

**评估**: ✅ 可通过状态判断是否发放积分，无需修改

### 3.2 新增表结构（可选扩展）

#### 3.2.1 积分变动历史表（可选）
```sql
-- 如需记录积分变动明细，可创建此表
CREATE TABLE IF NOT EXISTS `point_history` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户 ID',
  `change_type` VARCHAR(20) NOT NULL COMMENT '变动类型：EARN-获得，DEDUCT-扣减',
  `change_amount` INT NOT NULL COMMENT '变动金额',
  `balance_before` INT NOT NULL COMMENT '变动前余额',
  `balance_after` INT NOT NULL COMMENT '变动后余额',
  `source_type` VARCHAR(50) COMMENT '来源类型：ACTIVITY-活动，ORDER-订单',
  `source_id` BIGINT(20) COMMENT '来源 ID（活动 ID 或订单 ID）',
  `description` VARCHAR(500) COMMENT '变动说明',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` VARCHAR(50) COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_source` (`source_type`, `source_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分变动历史表';
```

**说明**: 
- 第一阶段可不实现，后续根据业务需要添加
- 如不需要历史记录，可跳过此表

---

## 4. 技术方案

### 4.1 整体架构

采用分层架构，在现有系统中增加积分相关业务逻辑：

```
┌─────────────────────────────────────┐
│     Controller Layer                │
│  - VolunteerActivityController      │
│  - OrderController                  │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│     Service Layer                   │
│  - VolunteerActivityService         │
│    ✓ completeActivity()             │
│    ✓ distributePoints()             │
│  - OrderService                     │
│    ✓ addOrder() (增强)              │
│  - PointService (新增)              │
│    ✓ earnPoints()                   │
│    ✓ deductPoints()                 │
│    ✓ getPointBalance()              │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│     Mapper Layer                    │
│  - VolunteerActivityMapper          │
│  - OrderMapper                      │
│  - VolunteerMapper (增强)           │
└─────────────────────────────────────┘
```

### 4.2 核心流程

#### 4.2.1 志愿活动积分发放流程

```
活动完成触发
    ↓
查询活动的所有正常参与者
    ↓
计算每个用户的积分（时长 × 10）
    ↓
[事务开始]
    ↓
更新用户积分余额
    ↓
标记活动为已完成（如需要）
    ↓
[事务提交]
    ↓
积分发放成功
```

#### 4.2.2 订单积分扣减流程

```
创建订单请求
    ↓
参数校验
    ↓
查询用户积分余额
    ↓
积分余额 >= 订单积分？
    ├─ NO → 返回错误："积分不足"
    └─ YES → 继续
    ↓
[事务开始]
    ↓
创建订单记录
    ↓
扣减用户积分
    ↓
[事务提交]
    ↓
订单创建成功
```

### 4.3 模块划分

#### 4.3.1 volunteerActivity-service 模块
**职责**: 志愿活动积分发放
- 新增活动完成接口
- 实现积分批量发放逻辑
- 更新活动状态（可选）

**新增方法**:
```java
// 完成活动并发放积分
void completeActivity(Long activityId);

// 批量发放积分（内部方法）
@Transactional
void distributePoints(Long activityId);
```

#### 4.3.2 shopping-service 模块
**职责**: 订单积分扣减
- 增强订单创建逻辑
- 积分余额校验
- 积分扣减

**修改方法**:
```java
// 增强现有 addOrder 方法
@Override
@Transactional
public void addOrder(OrderCreateDTO orderCreateDTO) {
    // 原有逻辑 + 积分扣减逻辑
}
```

#### 4.3.3 user-service 模块
**职责**: 积分账户管理
- 提供积分查询接口
- 提供积分变动接口

**新增方法**:
```java
// 增加积分
void addPoints(Long userId, Integer points, String sourceType, Long sourceId);

// 扣减积分
void deductPoints(Long userId, Integer points, String sourceType, Long sourceId);

// 查询积分余额
Integer getPointBalance(Long userId);
```

---

## 5. 详细实现方案

### 5.1 volunteerActivity-service 实现

#### 5.1.1 VolunteerActivityService 接口

```java
/**
 * 完成志愿活动并发放积分
 * @param activityId 活动 ID
 */
void completeActivity(Long activityId);

/**
 * 批量发放积分
 * @param activityId 活动 ID
 */
@Transactional
void distributePoints(Long activityId);
```

#### 5.1.2 VolunteerActivityServiceImpl 实现

```java
@Service
@Slf4j
public class VolunteerActivityServiceImpl implements VolunteerActivityService {
    
    @Autowired
    private VolunteerActivityMapper volunteerActivityMapper;
    
    @Autowired
    private VolunteerService volunteerService; // 需要新增 Feign 调用或本地服务
    
    /**
     * 完成志愿活动并发放积分
     */
    @Override
    @Transactional
    public void completeActivity(Long activityId) {
        log.info("完成志愿活动并发放积分：activityId-{}", activityId);
        
        // 1. 参数校验
        if (activityId == null || activityId <= 0) {
            throw new CommonException("活动 ID 不能为空");
        }
        
        // 2. 查询活动是否存在
        VolunteerActivity activity = volunteerActivityMapper.queryVolunteerActivityById(
            activityId, 
            VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED
        );
        if (activity == null) {
            throw new CommonException("活动不存在");
        }
        
        // 3. 检查活动状态（只能是进行中的活动可以完成）
        if (!VolunteerActivityCommonConstant.ACTIVITY_STATUS_IN_PROGRESS.equals(activity.getStatus())) {
            throw new CommonException("只有进行中的活动可以完成");
        }
        
        // 4. 更新活动状态为已结束
        activity.setStatus(VolunteerActivityCommonConstant.ACTIVITY_STATUS_COMPLETED);
        activity.setUpdateTime(new Date());
        activity.setUpdateUser(UserContext.getUsername());
        volunteerActivityMapper.updateVolunteerActivity(activity);
        
        // 5. 发放积分
        distributePoints(activityId);
        
        log.info("完成志愿活动并发放积分成功：activityId={}, 志愿时长={}", activityId, activity.getVolunteerHours());
    }
    
    /**
     * 批量发放积分
     */
    @Override
    @Transactional
    public void distributePoints(Long activityId) {
        log.info("批量发放积分：activityId-{}", activityId);
        
        // 1. 查询活动信息
        VolunteerActivity activity = volunteerActivityMapper.queryVolunteerActivityById(
            activityId, 
            VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED
        );
        if (activity == null) {
            throw new CommonException("活动不存在");
        }
        
        // 2. 计算总积分（时长 × 10）
        Double volunteerHours = activity.getVolunteerHours();
        if (volunteerHours == null || volunteerHours <= 0) {
            log.warn("活动志愿时长无效：activityId={}, hours={}", activityId, volunteerHours);
            return;
        }
        
        Integer totalPoints = (int) Math.round(volunteerHours * 10);
        
        // 3. 查询所有正常参与的参与者
        List<ActivityParticipant> participants = volunteerActivityMapper.queryNormalParticipantsByActivityId(activityId);
        if (participants == null || participants.isEmpty()) {
            log.warn("活动没有正常参与者：activityId={}", activityId);
            return;
        }
        
        // 4. 批量发放积分
        int successCount = 0;
        for (ActivityParticipant participant : participants) {
            try {
                // 通过 Feign 或本地服务调用增加积分
                volunteerService.addPoints(
                    participant.getUserId(), 
                    totalPoints, 
                    "ACTIVITY", 
                    activityId
                );
                successCount++;
                log.info("发放积分成功：userId={}, points={}, activityId={}", 
                    participant.getUserId(), totalPoints, activityId);
            } catch (Exception e) {
                log.error("发放积分失败：userId={}, points={}, activityId={}", 
                    participant.getUserId(), totalPoints, activityId, e);
                // 不中断整个流程，继续处理下一个用户
            }
        }
        
        log.info("批量发放积分完成：activityId={}, 应发人数={}, 成功人数={}", 
            activityId, participants.size(), successCount);
    }
}
```

#### 5.1.3 VolunteerActivityMapper 增强

```java
/**
 * 查询活动中所有正常参与的参与者
 * @param activityId 活动 ID
 * @return 参与者列表
 */
@Select("SELECT * FROM activity_participant WHERE activity_id = #{activityId} AND status = '1'")
List<ActivityParticipant> queryNormalParticipantsByActivityId(Long activityId);
```

#### 5.1.4 VolunteerActivityCommonConstant 常量

```java
/**
 * 活动状态 - 已完成
 */
public static final String ACTIVITY_STATUS_COMPLETED = "3";
```

### 5.2 shopping-service 实现

#### 5.2.1 OrderServiceImpl 增强

```java
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private VolunteerService volunteerService; // Feign 调用或本地服务
    
    /**
     * 新增订单（增强版：包含积分扣减）
     */
    @Override
    @Transactional
    public void addOrder(OrderCreateDTO orderCreateDTO) {
        log.info("添加订单：{}", orderCreateDTO);
        
        // 1. 参数校验
        CommonValidate.validateOrder(orderCreateDTO);
        
        // 2. 获取用户 ID（如果未传入）
        Long userId = orderCreateDTO.getUserId();
        if (userId == null) {
            userId = UserContext.getUserId();
            if (userId == null) {
                throw new CommonException("用户未登录");
            }
            orderCreateDTO.setUserId(userId);
        }
        
        // 3. 【新增】查询用户积分余额并校验
        Integer userPoints = volunteerService.getPointBalance(userId);
        if (userPoints == null || userPoints < 0) {
            throw new CommonException("用户积分查询失败");
        }
        
        Integer orderTotalPoints = orderCreateDTO.getPrice(); // 单商品，总价等于单价
        if (orderTotalPoints == null || orderTotalPoints <= 0) {
            throw new CommonException("订单积分必须大于 0");
        }
        
        if (userPoints < orderTotalPoints) {
            throw new CommonException("积分不足，当前积分：" + userPoints + ", 需要积分：" + orderTotalPoints);
        }
        
        // 4. 设置订单默认值
        Order order = new Order();
        BeanUtils.copyProperties(orderCreateDTO, order);
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setProductId(orderCreateDTO.getProductId());
        order.setProductName(orderCreateDTO.getProductName());
        order.setTotalIntegral(orderTotalPoints);
        order.setStatus(orderCreateDTO.getStatus());
        order.setShippingAddress(orderCreateDTO.getShippingAddress());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setCreateUser(UserContext.getUsername());
        order.setUpdateUser(UserContext.getUsername());
        
        // 5. 新增订单
        int result = orderMapper.addOrder(order);
        if (result == 0) {
            throw new CommonException("添加订单信息失败");
        }
        
        // 6. 【新增】扣减用户积分
        try {
            volunteerService.deductPoints(userId, orderTotalPoints, "ORDER", order.getId());
            log.info("扣减积分成功：userId={}, 扣减积分={}, 订单 ID={}", userId, orderTotalPoints, order.getId());
        } catch (Exception e) {
            log.error("扣减积分失败：userId={}, 扣减积分={}, 订单 ID={}", userId, orderTotalPoints, order.getId(), e);
            // 积分扣减失败，抛出异常回滚订单
            throw new CommonException("积分扣减失败，订单创建失败：" + e.getMessage());
        }
        
        log.info("添加订单成功，订单号：{}, 消耗积分：{}", order.getOrderNumber(), orderTotalPoints);
    }
}
```

### 5.3 user-service 实现

#### 5.3.1 VolunteerService 接口

```java
/**
 * 增加用户积分
 * @param userId 用户 ID
 * @param points 积分数量
 * @param sourceType 来源类型（ACTIVITY-活动，ORDER-订单）
 * @param sourceId 来源 ID（活动 ID 或订单 ID）
 */
void addPoints(Long userId, Integer points, String sourceType, Long sourceId);

/**
 * 扣减用户积分
 * @param userId 用户 ID
 * @param points 积分数量
 * @param sourceType 来源类型
 * @param sourceId 来源 ID
 */
void deductPoints(Long userId, Integer points, String sourceType, Long sourceId);

/**
 * 查询用户积分余额
 * @param userId 用户 ID
 * @return 积分余额
 */
Integer getPointBalance(Long userId);
```

#### 5.3.2 VolunteerServiceImpl 实现

```java
@Service
@Slf4j
public class VolunteerServiceImpl implements VolunteerService {
    
    @Autowired
    private VolunteerMapper volunteerMapper;
    
    /**
     * 增加用户积分
     */
    @Override
    @Transactional
    public void addPoints(Long userId, Integer points, String sourceType, Long sourceId) {
        log.info("增加用户积分：userId={}, points={}, sourceType={}, sourceId={}", 
            userId, points, sourceType, sourceId);
        
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new CommonException("用户 ID 不能为空");
        }
        if (points == null || points <= 0) {
            throw new CommonException("积分必须大于 0");
        }
        
        // 2. 查询用户志愿者信息
        Volunteer volunteer = volunteerMapper.queryVolunteerByUserId(userId);
        if (volunteer == null) {
            throw new CommonException("用户志愿者信息不存在");
        }
        
        // 3. 更新积分余额
        Integer currentPoints = volunteer.getActivityPoint();
        if (currentPoints == null) {
            currentPoints = 0;
        }
        
        Integer newPoints = currentPoints + points;
        
        VolunteerDTO volunteerDTO = new VolunteerDTO();
        volunteerDTO.setId(volunteer.getId());
        volunteerDTO.setActivityPoint(newPoints);
        volunteerDTO.setUpdateTime(new Date());
        volunteerDTO.setUpdateUser(UserContext.getUsername());
        
        int updated = volunteerMapper.updateVolunteerPoints(volunteerDTO);
        if (updated == 0) {
            throw new CommonException("更新积分余额失败");
        }
        
        // 4. 【可选】记录积分变动历史
        // pointHistoryService.recordHistory(userId, "EARN", points, currentPoints, newPoints, sourceType, sourceId);
        
        log.info("增加积分成功：userId={}, 原积分={}, 增加={}, 新积分={}", 
            userId, currentPoints, points, newPoints);
    }
    
    /**
     * 扣减用户积分
     */
    @Override
    @Transactional
    public void deductPoints(Long userId, Integer points, String sourceType, Long sourceId) {
        log.info("扣减用户积分：userId={}, points={}, sourceType={}, sourceId={}", 
            userId, points, sourceType, sourceId);
        
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new CommonException("用户 ID 不能为空");
        }
        if (points == null || points <= 0) {
            throw new CommonException("积分必须大于 0");
        }
        
        // 2. 查询用户志愿者信息
        Volunteer volunteer = volunteerMapper.queryVolunteerByUserId(userId);
        if (volunteer == null) {
            throw new CommonException("用户志愿者信息不存在");
        }
        
        // 3. 检查积分余额
        Integer currentPoints = volunteer.getActivityPoint();
        if (currentPoints == null || currentPoints < points) {
            throw new CommonException("积分余额不足");
        }
        
        // 4. 更新积分余额
        Integer newPoints = currentPoints - points;
        
        VolunteerDTO volunteerDTO = new VolunteerDTO();
        volunteerDTO.setId(volunteer.getId());
        volunteerDTO.setActivityPoint(newPoints);
        volunteerDTO.setUpdateTime(new Date());
        volunteerDTO.setUpdateUser(UserContext.getUsername());
        
        int updated = volunteerMapper.updateVolunteerPoints(volunteerDTO);
        if (updated == 0) {
            throw new CommonException("更新积分余额失败");
        }
        
        // 5. 【可选】记录积分变动历史
        // pointHistoryService.recordHistory(userId, "DEDUCT", points, currentPoints, newPoints, sourceType, sourceId);
        
        log.info("扣减积分成功：userId={}, 原积分={}, 扣减={}, 新积分={}", 
            userId, currentPoints, points, newPoints);
    }
    
    /**
     * 查询用户积分余额
     */
    @Override
    public Integer getPointBalance(Long userId) {
        if (userId == null || userId <= 0) {
            return 0;
        }
        
        Volunteer volunteer = volunteerMapper.queryVolunteerByUserId(userId);
        if (volunteer == null) {
            return 0;
        }
        
        Integer points = volunteer.getActivityPoint();
        return points != null ? points : 0;
    }
}
```

#### 5.3.3 VolunteerMapper 增强

```java
/**
 * 更新用户积分
 * @param volunteerDTO 志愿者信息（包含新的积分）
 * @return 影响的行数
 */
@Update("UPDATE volunteer SET activity_point = #{activityPoint}, update_user = #{updateUser}, update_time = now() WHERE id = #{id}")
int updateVolunteerPoints(VolunteerDTO volunteerDTO);
```

### 5.4 跨服务调用方案

#### 方案一：Feign 客户端（推荐，适用于微服务架构）

在 volunteerActivity-service 和 shopping-service 中创建 Feign 客户端：

```java
@FeignClient(name = "user-service", path = "/user/volunteer")
public interface VolunteerServiceClient {
    
    @PostMapping("/addPoints")
    ResultInfo addPoints(@RequestParam Long userId, 
                        @RequestParam Integer points, 
                        @RequestParam String sourceType, 
                        @RequestParam Long sourceId);
    
    @PostMapping("/deductPoints")
    ResultInfo deductPoints(@RequestParam Long userId, 
                           @RequestParam Integer points, 
                           @RequestParam String sourceType, 
                           @RequestParam Long sourceId);
    
    @GetMapping("/getPointBalance")
    ResultInfo<Integer> getPointBalance(@RequestParam Long userId);
}
```

#### 方案二：本地服务调用（适用于单体架构）

如果三个 service 在同一应用中，可直接注入服务：

```java
@Autowired
private com.hutb.user.service.VolunteerService volunteerService;
```

---

## 6. API 接口设计

### 6.1 志愿活动相关接口

#### 6.1.1 完成志愿活动并发放积分

**接口**: `POST /volunteerActivity/normalVolunteer/completeActivity`

**请求参数**:
```json
{
  "id": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**业务逻辑**:
1. 验证活动是否存在且处于进行中状态
2. 更新活动状态为已结束
3. 查询所有正常参与者
4. 批量发放积分（时长 × 10）
5. 记录积分发放日志

**错误码**:
- `400`: 活动 ID 不能为空
- `404`: 活动不存在
- `400`: 只有进行中的活动可以完成
- `500`: 积分发放失败

### 6.2 订单相关接口（增强）

#### 6.2.1 创建订单（已存在，增强积分扣减）

**接口**: `POST /shopping/order/normalVolunteer/createOrder`

**请求参数**:
```json
{
  "userId": 123,
  "productId": 456,
  "productName": "宠物粮",
  "price": 100,
  "shippingAddress": "xx 省 xx 市 xx 区"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": 789,
    "orderNumber": "ORD20260308123456789"
  }
}
```

**增强逻辑**:
1. 查询用户积分余额
2. 校验积分是否充足
3. 创建订单记录
4. 扣减用户积分
5. 返回订单信息

**错误码**:
- `400`: 用户未登录
- `400`: 积分不足，当前积分：50, 需要积分：100
- `400`: 订单积分必须大于 0
- `500`: 积分扣减失败，订单创建失败

### 6.3 积分查询接口（新增）

#### 6.3.1 查询用户积分余额

**接口**: `GET /user/volunteer/getPointBalance`

**请求参数**:
```
userId: 123
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 250
}
```

#### 6.3.2 查询积分变动历史（可选扩展）

**接口**: `POST /user/volunteer/pointHistory/list`

**请求参数**:
```json
{
  "userId": 123,
  "pageNum": 1,
  "pageSize": 10,
  "sourceType": "ACTIVITY"  // 可选
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "list": [
      {
        "id": 1,
        "changeType": "EARN",
        "changeAmount": 20,
        "balanceBefore": 230,
        "balanceAfter": 250,
        "sourceType": "ACTIVITY",
        "sourceId": 10,
        "description": "完成志愿活动：关爱流浪动物",
        "createTime": "2026-03-08 10:30:00"
      }
    ]
  }
}
```

---

## 7. 异常处理

### 7.1 积分发放异常

| 异常场景 | 异常信息 | 处理方式 |
|---------|---------|---------|
| 活动不存在 | "活动不存在" | 抛出 CommonException |
| 活动状态不正确 | "只有进行中的活动可以完成" | 抛出 CommonException |
| 无正常参与者 | 警告日志 | 记录日志，不抛异常 |
| 志愿时长无效 | 警告日志 | 记录日志，不抛异常 |
| 单个用户积分发放失败 | 错误日志 | 记录日志，继续处理下一用户 |
| 用户志愿者信息不存在 | "用户志愿者信息不存在" | 抛出 CommonException |

### 7.2 积分扣减异常

| 异常场景 | 异常信息 | 处理方式 |
|---------|---------|---------|
| 用户未登录 | "用户未登录" | 抛出 CommonException |
| 用户积分查询失败 | "用户积分查询失败" | 抛出 CommonException |
| 积分余额不足 | "积分不足，当前积分：X, 需要积分：Y" | 抛出 CommonException |
| 订单积分为 0 或负数 | "订单积分必须大于 0" | 抛出 CommonException |
| 积分扣减失败 | "积分扣减失败，订单创建失败" | 抛出 CommonException，回滚订单 |
| 用户志愿者信息不存在 | "用户志愿者信息不存在" | 抛出 CommonException |

### 7.3 全局异常处理

在 Controller 层统一捕获异常：

```java
@PostMapping("normalVolunteer/completeActivity")
public ResultInfo completeActivity(@RequestParam Long id) {
    try {
        volunteerActivityService.completeActivity(id);
        return ResultInfo.success();
    } catch (CommonException e) {
        log.error("完成活动失败：{}", e.getMessage());
        return ResultInfo.fail(e.getMessage());
    } catch (Exception e) {
        log.error("系统错误：{}", e.getMessage(), e);
        return ResultInfo.fail("系统错误：" + e.getMessage());
    }
}
```

---

## 8. 测试策略

### 8.1 单元测试

#### 8.1.1 志愿活动积分发放测试

```java
@SpringBootTest
class VolunteerActivityServiceImplTest {
    
    @Autowired
    private VolunteerActivityService volunteerActivityService;
    
    @Autowired
    private VolunteerMapper volunteerMapper;
    
    /**
     * 测试正常完成活动并发放积分
     */
    @Test
    void testCompleteActivity_Success() {
        // 1. 准备数据
        Long activityId = 1L;
        VolunteerActivity activity = new VolunteerActivity();
        activity.setId(activityId);
        activity.setVolunteerHours(2.0);
        activity.setStatus("2"); // 进行中
        
        // 2. 执行
        volunteerActivityService.completeActivity(activityId);
        
        // 3. 验证
        Volunteer volunteer = volunteerMapper.queryVolunteerByUserId(100L);
        assertEquals(20, volunteer.getActivityPoint().intValue()); // 2 小时 × 10 = 20 积分
    }
    
    /**
     * 测试活动不存在
     */
    @Test
    void testCompleteActivity_NotExist() {
        assertThrows(CommonException.class, () -> {
            volunteerActivityService.completeActivity(999L);
        });
    }
    
    /**
     * 测试活动状态不正确
     */
    @Test
    void testCompleteActivity_WrongStatus() {
        // 准备一个已结束的活动
        Long activityId = 2L;
        // ...
        
        assertThrows(CommonException.class, () -> {
            volunteerActivityService.completeActivity(activityId);
        });
    }
}
```

#### 8.1.2 订单积分扣减测试

```java
@SpringBootTest
class OrderServiceImplTest {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private VolunteerMapper volunteerMapper;
    
    /**
     * 测试积分充足，订单创建成功
     */
    @Test
    void testAddOrder_SufficientPoints() {
        // 1. 准备数据
        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setUserId(100L);
        orderDTO.setProductId(1L);
        orderDTO.setPrice(50); // 需要 50 积分
        orderDTO.setShippingAddress("测试地址");
        
        // 用户有 100 积分
        VolunteerDTO volunteer = volunteerMapper.queryVolunteerByUserId(100L);
        volunteer.setActivityPoint(100);
        volunteerMapper.updateVolunteerPoints(volunteer);
        
        // 2. 执行
        orderService.addOrder(orderDTO);
        
        // 3. 验证
        Volunteer updatedVolunteer = volunteerMapper.queryVolunteerByUserId(100L);
        assertEquals(50, updatedVolunteer.getActivityPoint().intValue()); // 100 - 50 = 50
    }
    
    /**
     * 测试积分不足，订单创建失败
     */
    @Test
    void testAddOrder_InsufficientPoints() {
        // 1. 准备数据
        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setUserId(101L);
        orderDTO.setPrice(100); // 需要 100 积分
        
        // 用户只有 30 积分
        VolunteerDTO volunteer = volunteerMapper.queryVolunteerByUserId(101L);
        volunteer.setActivityPoint(30);
        volunteerMapper.updateVolunteerPoints(volunteer);
        
        // 2. 执行并验证
        CommonException exception = assertThrows(CommonException.class, () -> {
            orderService.addOrder(orderDTO);
        });
        
        assertTrue(exception.getMessage().contains("积分不足"));
    }
}
```

### 8.2 集成测试

#### 8.2.1 完整流程测试

```java
@SpringBootTest
class PointSystemIntegrationTest {
    
    /**
     * 测试完整流程：参与活动 → 获得积分 → 消费积分
     */
    @Test
    void testPointFlow_EndToEnd() {
        // 1. 用户报名参与活动
        Long activityId = 1L;
        volunteerActivityService.joinActivity(activityId);
        
        // 2. 管理员完成活动并发放积分
        volunteerActivityService.completeActivity(activityId);
        
        // 3. 查询用户积分（应该增加 20 积分）
        Integer points = volunteerService.getPointBalance(100L);
        assertEquals(20, points.intValue());
        
        // 4. 用户创建订单消费积分
        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setUserId(100L);
        orderDTO.setPrice(15); // 消费 15 积分
        orderDTO.setShippingAddress("测试地址");
        
        orderService.addOrder(orderDTO);
        
        // 5. 验证剩余积分
        Integer remainingPoints = volunteerService.getPointBalance(100L);
        assertEquals(5, remainingPoints.intValue()); // 20 - 15 = 5
    }
}
```

### 8.3 边界测试

```java
@Test
void testBoundaryCases() {
    // 1. 测试 0 时长活动（不发放积分）
    // 2. 测试积分刚好足够（边界情况）
    // 3. 测试积分扣减为 0
    // 4. 测试并发场景下的积分扣减
}
```

---

## 9. 部署方案

### 9.1 部署步骤

#### 阶段一：准备工作
1. **数据库备份**
   ```bash
   mysqldump -u root -p animal_manage > backup_$(date +%Y%m%d).sql
   ```

2. **验证现有数据**
   ```sql
   -- 检查 volunteer 表中 activity_point 字段
   SELECT COUNT(*) FROM volunteer WHERE activity_point IS NOT NULL;
   
   -- 检查是否有脏数据
   SELECT * FROM volunteer WHERE activity_point < 0;
   ```

#### 阶段二：代码部署
1. **更新 user-service**
   - 部署 VolunteerService 积分相关接口
   - 更新 VolunteerMapper
   - 验证积分查询、增加、扣减功能

2. **更新 volunteerActivity-service**
   - 部署活动完成接口
   - 部署积分批量发放逻辑
   - 更新 VolunteerActivityMapper

3. **更新 shopping-service**
   - 增强订单创建逻辑
   - 集成积分扣减功能
   - 验证积分校验和扣减

#### 阶段三：验证测试
```bash
# 1. 验证积分发放
curl -X POST http://localhost:8080/volunteerActivity/normalVolunteer/completeActivity?id=1

# 2. 验证积分查询
curl -X GET "http://localhost:8080/user/volunteer/getPointBalance?userId=100"

# 3. 验证订单创建和积分扣减
curl -X POST http://localhost:8080/shopping/order/normalVolunteer/createOrder \
  -H "Content-Type: application/json" \
  -d '{"userId":100,"productId":1,"price":50,"shippingAddress":"测试地址"}'
```

### 9.2 回滚方案

如遇到问题，按以下步骤回滚：

1. **停止服务**
   ```bash
   systemctl stop user-service
   systemctl stop volunteerActivity-service
   systemctl stop shopping-service
   ```

2. **恢复代码**
   ```bash
   # 回滚到上一个版本
   git checkout <previous-commit>
   mvn clean package
   ```

3. **恢复数据**
   ```bash
   mysql -u root -p animal_manage < backup_YYYYMMDD.sql
   ```

4. **重启服务**
   ```bash
   systemctl start user-service
   systemctl start volunteerActivity-service
   systemctl start shopping-service
   ```

### 9.3 监控指标

部署后重点关注以下指标：

1. **积分发放成功率**
   - 目标：> 99%
   - 监控：发放成功人数 / 应发放人数

2. **积分扣减成功率**
   - 目标：> 99%
   - 监控：扣减成功订单数 / 总订单数

3. **积分不足拒绝率**
   - 监控：因积分不足被拒订单数 / 总订单数
   - 分析：用户积分获取和消费是否平衡

4. **接口响应时间**
   - 积分查询：< 100ms
   - 积分发放：< 500ms
   - 订单创建：< 300ms

---

## 10. 安全考虑

### 10.1 积分安全

1. **防止积分超扣**
   - 数据库层面：积分字段设置为无符号整数
   - 应用层面：扣减前严格校验余额
   - 事务层面：确保扣减操作的原子性

2. **防止重复发放**
   - 活动完成接口幂等性设计
   - 记录已完成的活动 ID
   - 检查活动状态（只能完成一次）

3. **防止恶意刷分**
   - 限制单个活动的最大积分
   - 监控异常积分变动（如短时间大量获得积分）
   - 审计日志记录所有积分变动

### 10.2 接口安全

1. **权限控制**
   - 完成活动接口：仅管理员可调用
   - 积分查询接口：仅本人或管理员可调用
   - 订单创建接口：需登录验证

2. **参数校验**
   - 严格校验用户 ID、活动 ID 等参数
   - 防止 SQL 注入
   - 防止 XSS 攻击

3. **限流措施**
   - 对积分相关接口实施限流
   - 防止恶意刷接口

### 10.3 数据安全

1. **敏感数据加密**
   - 积分变动日志脱敏存储
   - 用户 ID 加密传输

2. **数据备份**
   - 定期备份积分相关数据
   - 建立数据恢复机制

3. **审计日志**
   ```sql
   -- 记录所有积分变动
   INSERT INTO point_history (...) VALUES (...);
   ```

---

## 11. 扩展功能（可选）

### 11.1 短期扩展（1-2 周）

#### 11.1.1 积分变动历史记录
- 创建 `point_history` 表
- 记录每笔积分变动
- 提供积分明细查询接口

#### 11.1.2 积分过期机制
- 设置积分有效期（如 1 年）
- 定期清理过期积分
- 提前通知用户即将过期的积分

#### 11.1.3 订单取消积分返还
- 订单取消时自动返还积分
- 记录积分返还日志
- 支持部分退款（部分返还积分）

### 11.2 中期扩展（1-2 月）

#### 11.2.1 积分商城升级
- 支持多种积分兑换方式
- 积分 + 现金混合支付
- 积分优惠券

#### 11.2.2 积分等级体系
- 根据积分划分用户等级
- 不同等级享受不同权益
- 等级特权（如优先参与活动）

#### 11.2.3 积分任务系统
- 每日签到得积分
- 邀请好友得积分
- 完成特定任务得积分

### 11.3 长期扩展（3-6 月）

#### 11.3.1 积分交易系统
- 用户间积分转让
- 积分拍卖
- 积分兑换实物

#### 11.3.2 积分数据分析
- 用户积分行为分析
- 积分发放和消耗统计
- 积分系统 ROI 分析

#### 11.3.3 积分营销活动
- 积分翻倍活动
- 限时兑换活动
- 积分抽奖

---

## 12. 注意事项

### 12.1 开发注意事项

1. ⚠️ **事务管理**
   - 积分变动必须使用 `@Transactional` 注解
   - 确保积分扣减和订单创建在同一事务中
   - 异常情况下正确回滚

2. ⚠️ **并发控制**
   - 考虑使用乐观锁（版本号机制）
   - 或使用分布式锁（Redis 锁）
   - 防止并发场景下的积分超扣

3. ⚠️ **空值处理**
   - 积分字段可能为 null，需做判空处理
   - 默认积分设为 0

4. ⚠️ **日志记录**
   - 关键操作记录详细日志
   - 便于问题排查和审计

### 12.2 测试注意事项

1. ⚠️ **边界条件**
   - 测试积分为 0 的场景
   - 测试积分刚好足够的场景
   - 测试积分不足的场景

2. ⚠️ **异常场景**
   - 测试网络超时
   - 测试数据库连接失败
   - 测试并发请求

3. ⚠️ **数据一致性**
   - 验证事务回滚是否正常
   - 验证积分变动是否正确

### 12.3 运维注意事项

1. ⚠️ **监控告警**
   - 设置积分异常变动告警
   - 监控积分发放失败率
   - 监控积分扣减失败率

2. ⚠️ **数据备份**
   - 定期备份积分相关表
   - 建立数据恢复演练机制

3. ⚠️ **性能优化**
   - 关注慢查询日志
   - 优化积分查询 SQL
   - 必要时添加缓存

---

## 13. 附录

### 13.1 相关文件清单

#### 新增文件
1. `openspec/point-system-spec.md` - 本文档
2. `user-service/src/main/java/com/hutb/user/service/VolunteerService.java` - 积分服务接口
3. `user-service/src/main/java/com/hutb/user/service/impl/VolunteerServiceImpl.java` - 积分服务实现

#### 修改文件
1. `volunteerActivity-service/src/main/java/com/hutb/volunteerActivity/service/VolunteerActivityService.java`
2. `volunteerActivity-service/src/main/java/com/hutb/volunteerActivity/service/impl/VolunteerActivityServiceImpl.java`
3. `volunteerActivity-service/src/main/java/com/hutb/volunteerActivity/mapper/VolunteerActivityMapper.java`
4. `shopping-service/src/main/java/com/hutb/shopping/service/impl/OrderServiceImpl.java`
5. `user-service/src/main/java/com/hutb/user/mapper/VolunteerMapper.java`
6. `volunteerActivity-service/src/main/java/com/hutb/volunteerActivity/constant/VolunteerActivityCommonConstant.java`

### 13.2 参考文档
- 志愿活动加入功能：`openspec/volunteer-activity-join-spec.md`
- 志愿活动取消功能：`openspec/volunteer-activity-cancel-and-query-spec.md`
- 订单管理功能：`openspec/order-management-spec/design.md`

### 13.3 术语表

| 术语 | 英文 | 说明 |
|-----|------|-----|
| 积分 | Point | 用户在系统中的虚拟货币 |
| 志愿时长 | Volunteer Hours | 参与志愿活动的时间（小时） |
| 积分系数 | Point Rate | 每小时志愿时长兑换的积分（默认 10） |
| 发放 | Distribute | 将积分添加到用户账户 |
| 扣减 | Deduct | 从用户账户扣除积分 |

---

**文档版本**: v1.0  
**创建时间**: 2026-03-08  
**审核状态**: 待审核  
**实施状态**: 待实施
