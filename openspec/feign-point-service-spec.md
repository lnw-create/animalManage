# 积分查询 Feign 客户端实现规格变更

## 1. 变更概述

### 1.1 变更背景
当前 shopping-service 的 OrderServiceImpl 中存在硬编码的临时方法 `getUserPointBalance()` 和 `deductUserPoints()`，需要实现通过 Feign 客户端调用 user-service 来完成积分查询和扣减功能，以支持微服务架构下的跨服务调用。

### 1.2 变更范围
- 在 user-service 中新增积分管理 REST API 接口
- 在 shopping-service 中创建 Feign 客户端接口
- 修改 OrderServiceImpl 使用 Feign 客户端替代临时方法
- 添加必要的依赖配置和错误处理机制

### 1.3 当前状态
- ✅ user-service 已有 VolunteerService 接口定义积分相关方法
- ✅ user-service 已有 VolunteerServiceImpl 实现积分逻辑
- ✅ volunteer 表已有 activity_point 字段存储积分余额
- ❌ user-service 缺少对外暴露的 REST API 接口
- ❌ shopping-service 缺少 Feign 客户端配置
- ❌ OrderServiceImpl 使用临时方法模拟积分操作

---

## 2. 需求分析

### 2.1 功能需求

#### 2.1.1 积分查询接口
- 提供 HTTP GET 接口查询用户积分余额
- 支持通过 userId 参数查询
- 返回明确的积分数值或错误信息

#### 2.1.2 积分扣减接口
- 提供 HTTP POST 接口扣减用户积分
- 支持传入 userId、points、sourceType、sourceId 参数
- 积分不足时抛出明确的错误信息
- 扣减成功后返回操作结果

#### 2.1.3 积分增加接口
- 提供 HTTP POST 接口增加用户积分
- 支持传入 userId、points、sourceType、sourceId 参数
- 用于志愿活动完成后发放积分

### 2.2 非功能需求

#### 2.2.1 性能要求
- 积分查询接口响应时间 < 100ms
- 积分扣减接口响应时间 < 200ms
- 支持高并发场景下的积分查询

#### 2.2.2 一致性要求
- 积分扣减必须使用事务保证数据一致性
- 防止并发场景下的积分超扣问题
- 积分变动必须有完整的日志记录

#### 2.2.3 安全性要求
- 积分相关接口需要权限验证（通过 Gateway 统一鉴权）
- 防止恶意调用积分接口
- 敏感操作需要记录操作日志

---

## 3. 技术方案

### 3.1 整体架构

```
┌─────────────────────────┐         ┌─────────────────────────┐
│   shopping-service      │         │    user-service         │
│                         │         │                         │
│  ┌─────────────────┐    │         │  ┌─────────────────┐    │
│  │ OrderController │    │         │  │VolunteerController│  │
│  └────────┬────────┘    │         │  └────────┬────────┘    │
│           │             │         │           │             │
│  ┌────────▼────────┐    │         │  ┌────────▼────────┐    │
│  │ OrderServiceImpl│    │         │  │VolunteerServiceImpl│ │
│  └────────┬────────┘    │         │  └────────┬────────┘    │
│           │             │         │           │             │
│  ┌────────▼────────┐    │  HTTP   │  ┌────────▼────────┐    │
│  │VolunteerService │────Feign────▶│  │   Mapper Layer  │    │
│  │    Client       │    │  Rest   │  │                 │    │
│  └─────────────────┘    │  Request│  └─────────────────┘    │
└─────────────────────────┘         └─────────────────────────┘
```

### 3.2 核心流程

#### 3.2.1 积分查询流程
```
OrderServiceImpl.createOrder()
    ↓
调用 VolunteerServiceClient.getPointBalance(userId)
    ↓
发送 HTTP GET 请求到 user-service
    ↓
VolunteerController.getPointBalance()
    ↓
VolunteerServiceImpl.getPointBalance()
    ↓
查询 volunteer 表获取积分余额
    ↓
返回积分数值
```

#### 3.2.2 积分扣减流程
```
OrderServiceImpl.createOrder()
    ↓
调用 VolunteerServiceClient.deductPoints(userId, points, sourceType, sourceId)
    ↓
发送 HTTP POST 请求到 user-service
    ↓
VolunteerController.deductPoints()
    ↓
VolunteerServiceImpl.deductPoints() [@Transactional]
    ↓
检查积分余额是否充足
    ↓
扣减积分并记录日志
    ↓
返回操作结果
```

---

## 4. 详细设计

### 4.1 user-service 改动

#### 4.1.1 VolunteerController 新增接口

**文件路径**: `user-service/src/main/java/com/hutb/user/controller/VolunteerController.java`

**新增方法**:

```java
/**
 * 查询用户积分余额
 * @param userId 用户 ID
 * @return 积分余额
 */
@GetMapping("point/getPointBalance")
public ResultInfo<Integer> getPointBalance(@RequestParam Long userId) {
    try {
        Integer balance = volunteerService.getPointBalance(userId);
        return ResultInfo.success(balance);
    } catch (CommonException e) {
        log.error("查询积分余额失败：userId={}", userId, e);
        return ResultInfo.fail(e.getMessage());
    } catch (Exception e) {
        log.error("系统错误：userId={}", userId, e);
        return ResultInfo.fail("系统错误：" + e.getMessage());
    }
}

/**
 * 扣减用户积分
 * @param userId 用户 ID
 * @param points 积分数量
 * @param sourceType 来源类型（ORDER-订单，ACTIVITY-活动）
 * @param sourceId 来源 ID
 * @return 操作结果
 */
@PostMapping("point/deductPoints")
public ResultInfo<Void> deductPoints(
    @RequestParam Long userId,
    @RequestParam Integer points,
    @RequestParam String sourceType,
    @RequestParam Long sourceId
) {
    try {
        volunteerService.deductPoints(userId, points, sourceType, sourceId);
        return ResultInfo.success();
    } catch (CommonException e) {
        log.error("扣减积分失败：userId={}, points={}", userId, points, e);
        return ResultInfo.fail(e.getMessage());
    } catch (Exception e) {
        log.error("系统错误：userId={}, points={}", userId, points, e);
        return ResultInfo.fail("系统错误：" + e.getMessage());
    }
}

/**
 * 增加用户积分
 * @param userId 用户 ID
 * @param points 积分数量
 * @param sourceType 来源类型（ORDER-订单，ACTIVITY-活动）
 * @param sourceId 来源 ID
 * @return 操作结果
 */
@PostMapping("point/addPoints")
public ResultInfo<Void> addPoints(
    @RequestParam Long userId,
    @RequestParam Integer points,
    @RequestParam String sourceType,
    @RequestParam Long sourceId
) {
    try {
        volunteerService.addPoints(userId, points, sourceType, sourceId);
        return ResultInfo.success();
    } catch (CommonException e) {
        log.error("增加积分失败：userId={}, points={}", userId, points, e);
        return ResultInfo.fail(e.getMessage());
    } catch (Exception e) {
        log.error("系统错误：userId={}, points={}", userId, points, e);
        return ResultInfo.fail("系统错误：" + e.getMessage());
    }
}
```

### 4.2 shopping-service 改动

#### 4.2.1 添加 Spring Cloud OpenFeign 依赖

**文件路径**: `shopping-service/pom.xml`

**新增依赖**:

```xml
<!-- Spring Cloud OpenFeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### 4.2.2 创建 Feign 客户端接口

**文件路径**: `shopping-service/src/main/java/com/hutb/shopping/client/VolunteerServiceClient.java`

```java
package com.hutb.shopping.client;

import com.hutb.shopping.model.pojo.ResultInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 志愿者服务 Feign 客户端
 * 用于调用 user-service 的积分管理接口
 */
@FeignClient(name = "user-service", path = "/userService/volunteer")
public interface VolunteerServiceClient {

    /**
     * 查询用户积分余额
     * @param userId 用户 ID
     * @return 积分余额
     */
    @GetMapping("point/getPointBalance")
    ResultInfo<Integer> getPointBalance(@RequestParam Long userId);

    /**
     * 扣减用户积分
     * @param userId 用户 ID
     * @param points 积分数量
     * @param sourceType 来源类型（ORDER-订单，ACTIVITY-活动）
     * @param sourceId 来源 ID
     * @return 操作结果
     */
    @PostMapping("point/deductPoints")
    ResultInfo<Void> deductPoints(
        @RequestParam Long userId,
        @RequestParam Integer points,
        @RequestParam String sourceType,
        @RequestParam Long sourceId
    );

    /**
     * 增加用户积分
     * @param userId 用户 ID
     * @param points 积分数量
     * @param sourceType 来源类型（ORDER-订单，ACTIVITY-活动）
     * @param sourceId 来源 ID
     * @return 操作结果
     */
    @PostMapping("point/addPoints")
    ResultInfo<Void> addPoints(
        @RequestParam Long userId,
        @RequestParam Integer points,
        @RequestParam String sourceType,
        @RequestParam Long sourceId
    );
}
```

#### 4.2.3 启用 Feign 客户端

**文件路径**: `shopping-service/src/main/java/com/hutb/shopping/ShoppingApplication.java`

```java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.hutb.shopping.client") // 新增注解
public class ShoppingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingApplication.class, args);
    }
}
```

#### 4.2.4 修改 OrderServiceImpl

**文件路径**: `shopping-service/src/main/java/com/hutb/shopping/service/impl/OrderServiceImpl.java`

**修改内容**:

1. **注入 Feign 客户端**:
```java
@Autowired
private VolunteerServiceClient volunteerServiceClient;
```

2. **替换 getUserPointBalance 方法**:
```java
/**
 * 查询用户积分余额
 * @param userId 用户 ID
 * @return 积分余额
 */
private Integer getUserPointBalance(Long userId) {
    ResultInfo<Integer> result = volunteerServiceClient.getPointBalance(userId);
    
    // 检查远程调用结果
    if (result == null || result.getCode() != 200) {
        log.error("查询积分余额失败：userId={}, message={}", userId, 
            result != null ? result.getMessage() : "unknown error");
        throw new CommonException("查询积分余额失败：" + 
            (result != null ? result.getMessage() : "unknown error"));
    }
    
    Integer balance = result.getData();
    if (balance == null) {
        balance = 0;
    }
    
    log.info("查询积分余额成功：userId={}, balance={}", userId, balance);
    return balance;
}
```

3. **替换 deductUserPoints 方法**:
```java
/**
 * 扣减用户积分
 * @param userId 用户 ID
 * @param points 积分数量
 * @param sourceType 来源类型（ORDER-订单，ACTIVITY-活动）
 * @param sourceId 来源 ID
 */
private void deductUserPoints(Long userId, Integer points, String sourceType, Long sourceId) {
    ResultInfo<Void> result = volunteerServiceClient.deductPoints(userId, points, sourceType, sourceId);
    
    // 检查远程调用结果
    if (result == null || result.getCode() != 200) {
        log.error("扣减积分失败：userId={}, points={}, message={}", userId, points,
            result != null ? result.getMessage() : "unknown error");
        throw new CommonException("扣减积分失败：" + 
            (result != null ? result.getMessage() : "unknown error"));
    }
    
    log.info("扣减积分成功：userId={}, points={}, sourceType={}, sourceId={}", 
        userId, points, sourceType, sourceId);
}
```

4. **删除原有的临时方法实现**（第 110-129 行）

### 4.3 配置优化（可选）

#### 4.3.1 Feign 超时配置

**文件路径**: `shopping-service/src/main/resources/application.yaml`

```yaml
feign:
  client:
    config:
      user-service:
        connectTimeout: 5000  # 连接超时时间（毫秒）
        readTimeout: 10000    # 读取超时时间（毫秒）
  compression:
    request:
      enabled: true  # 启用请求压缩
    response:
      enabled: true  # 启用响应压缩
```

#### 4.3.2 负载均衡配置

如果部署多个 user-service 实例，Ribbon 会自动进行负载均衡。如需自定义配置：

```yaml
user-service:
  ribbon:
    ReadTimeout: 10000
    ConnectTimeout: 5000
    OkHttp:
      enabled: true
```

---

## 5. API 接口设计

### 5.1 查询用户积分余额

**接口地址**: `GET /userService/volunteer/point/getPointBalance`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户 ID |

**请求示例**:
```http
GET /userService/volunteer/point/getPointBalance?userId=123
Host: localhost:8080
```

**响应示例 - 成功**:
```json
{
  "code": 200,
  "message": "success",
  "data": 250
}
```

**响应示例 - 失败**:
```json
{
  "code": 500,
  "message": "用户志愿者信息不存在",
  "data": null
}
```

### 5.2 扣减用户积分

**接口地址**: `POST /userService/volunteer/point/deductPoints`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户 ID |
| points | Integer | 是 | 积分数量 |
| sourceType | String | 是 | 来源类型（ORDER-订单，ACTIVITY-活动） |
| sourceId | Long | 是 | 来源 ID |

**请求示例**:
```http
POST /userService/volunteer/point/deductPoints?userId=123&points=100&sourceType=ORDER&sourceId=456
Host: localhost:8080
```

**响应示例 - 成功**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**响应示例 - 失败**:
```json
{
  "code": 500,
  "message": "积分余额不足，当前积分：50, 需要积分：100",
  "data": null
}
```

### 5.3 增加用户积分

**接口地址**: `POST /userService/volunteer/point/addPoints`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户 ID |
| points | Integer | 是 | 积分数量 |
| sourceType | String | 是 | 来源类型（ORDER-订单，ACTIVITY-活动） |
| sourceId | Long | 是 | 来源 ID |

**请求示例**:
```http
POST /userService/volunteer/point/addPoints?userId=123&points=20&sourceType=ACTIVITY&sourceId=789
Host: localhost:8080
```

**响应示例 - 成功**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 6. 异常处理

### 6.1 Feign 调用异常

| 异常类型 | 触发场景 | 处理方式 |
|---------|---------|---------|
| FeignException$ServiceUnavailable | user-service 不可用 | 抛出 CommonException，提示"积分服务暂时不可用" |
| FeignException$BadRequest | 请求参数错误 | 抛出 CommonException，提示具体参数错误信息 |
| FeignException$InternalServerError | user-service 内部错误 | 抛出 CommonException，提示"积分操作失败" |
| ReadTimeoutException | 读取超时 | 抛出 CommonException，提示"积分服务响应超时" |

### 6.2 业务异常

| 异常场景 | 异常信息 | HTTP 状态码 |
|---------|---------|------------|
| 用户 ID 为空 | "用户 ID 不能为空" | 400 |
| 积分必须大于 0 | "积分必须大于 0" | 400 |
| 积分余额不足 | "积分余额不足，当前积分：X, 需要积分：Y" | 400 |
| 用户志愿者信息不存在 | "用户志愿者信息不存在" | 404 |
| 更新积分余额失败 | "更新积分余额失败" | 500 |

### 6.3 降级策略（可选扩展）

如需实现服务降级，可创建 Fallback 类：

```java
@Component
public class VolunteerServiceClientFallback implements VolunteerServiceClient {
    
    @Override
    public ResultInfo<Integer> getPointBalance(Long userId) {
        return ResultInfo.fail("积分服务暂时不可用，请稍后重试");
    }
    
    @Override
    public ResultInfo<Void> deductPoints(Long userId, Integer points, String sourceType, Long sourceId) {
        return ResultInfo.fail("积分服务暂时不可用，请稍后重试");
    }
    
    @Override
    public ResultInfo<Void> addPoints(Long userId, Integer points, String sourceType, Long sourceId) {
        return ResultInfo.fail("积分服务暂时不可用，请稍后重试");
    }
}
```

在 FeignClient 注解中指定 fallback：
```java
@FeignClient(name = "user-service", path = "/userService/volunteer", fallback = VolunteerServiceClientFallback.class)
```

---

## 7. 测试策略

### 7.1 单元测试

#### 7.1.1 VolunteerController 测试

**测试类**: `user-service/src/test/java/com/hutb/user/controller/VolunteerControllerTest.java`

```java
@SpringBootTest
@AutoConfigureMockMvc
class VolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VolunteerMapper volunteerMapper;

    /**
     * 测试查询积分余额 - 成功场景
     */
    @Test
    void testGetPointBalance_Success() throws Exception {
        // 准备数据
        Long userId = 100L;
        Volunteer volunteer = new Volunteer();
        volunteer.setUserId(userId);
        volunteer.setActivityPoint(500);
        // 插入测试数据...

        // 执行请求
        mockMvc.perform(get("/userService/volunteer/point/getPointBalance")
                .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(500));
    }

    /**
     * 测试查询积分余额 - 用户不存在
     */
    @Test
    void testGetPointBalance_UserNotFound() throws Exception {
        mockMvc.perform(get("/userService/volunteer/point/getPointBalance")
                .param("userId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").contains("用户志愿者信息不存在"));
    }

    /**
     * 测试扣减积分 - 成功场景
     */
    @Test
    void testDeductPoints_Success() throws Exception {
        Long userId = 100L;
        Integer points = 100;
        // 准备数据：用户有 500 积分

        mockMvc.perform(post("/userService/volunteer/point/deductPoints")
                .param("userId", String.valueOf(userId))
                .param("points", String.valueOf(points))
                .param("sourceType", "ORDER")
                .param("sourceId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证积分已扣减
        Volunteer volunteer = volunteerMapper.queryVolunteerByUserId(userId);
        assertEquals(400, volunteer.getActivityPoint().intValue());
    }

    /**
     * 测试扣减积分 - 积分不足
     */
    @Test
    void testDeductPoints_InsufficientPoints() throws Exception {
        Long userId = 100L;
        Integer points = 1000; // 用户只有 500 积分

        mockMvc.perform(post("/userService/volunteer/point/deductPoints")
                .param("userId", String.valueOf(userId))
                .param("points", String.valueOf(points))
                .param("sourceType", "ORDER")
                .param("sourceId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").contains("积分余额不足"));
    }
}
```

#### 7.1.2 OrderServiceImpl 测试

**测试类**: `shopping-service/src/test/java/com/hutb/shopping/service/impl/OrderServiceImplTest.java`

```java
@SpringBootTest
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private VolunteerServiceClient volunteerServiceClient;

    /**
     * 测试创建订单 - 积分充足
     */
    @Test
    void testAddOrder_Success() {
        // 准备数据
        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setProductId(1L);
        orderDTO.setProductName("测试商品");
        orderDTO.setPrice(100);
        orderDTO.setShippingAddress("测试地址");

        // Mock Feign 调用返回
        ResultInfo<Integer> balanceResult = ResultInfo.success(500);
        when(volunteerServiceClient.getPointBalance(anyLong())).thenReturn(balanceResult);
        
        ResultInfo<Void> deductResult = ResultInfo.success();
        when(volunteerServiceClient.deductPoints(anyLong(), anyInt(), anyString(), anyLong()))
            .thenReturn(deductResult);

        // 执行
        assertDoesNotThrow(() -> orderService.addOrder(orderDTO));
    }

    /**
     * 测试创建订单 - 积分不足
     */
    @Test
    void testAddOrder_InsufficientPoints() {
        // 准备数据
        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setPrice(100);

        // Mock 积分不足
        ResultInfo<Integer> balanceResult = ResultInfo.success(50);
        when(volunteerServiceClient.getPointBalance(anyLong())).thenReturn(balanceResult);

        // 执行并验证异常
        CommonException exception = assertThrows(CommonException.class, 
            () -> orderService.addOrder(orderDTO));
        assertTrue(exception.getMessage().contains("积分不足"));
    }
}
```

### 7.2 集成测试

#### 7.2.1 Feign 客户端集成测试

**测试类**: `shopping-service/src/test/java/com/hutb/shopping/client/VolunteerServiceClientIntegrationTest.java`

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class VolunteerServiceClientIntegrationTest {

    @Autowired
    private VolunteerServiceClient volunteerServiceClient;

    @Value("${wiremock.server.baseUrl}")
    private String wireMockBaseUrl;

    /**
     * 测试 Feign 客户端调用 - 查询积分余额
     */
    @Test
    void testGetPointBalance_Integration() {
        // 配置 WireMock 桩
        stubFor(get(urlEqualTo("/userService/volunteer/point/getPointBalance?userId=123"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"code\":200,\"message\":\"success\",\"data\":350}")));

        // 执行调用
        ResultInfo<Integer> result = volunteerServiceClient.getPointBalance(123L);

        // 验证
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(350, result.getData().intValue());
    }

    /**
     * 测试 Feign 客户端调用 - 扣减积分
     */
    @Test
    void testDeductPoints_Integration() {
        // 配置 WireMock 桩
        stubFor(post(urlPathEqualTo("/userService/volunteer/point/deductPoints"))
            .withQueryParam("userId", equalTo("123"))
            .withQueryParam("points", equalTo("100"))
            .withQueryParam("sourceType", equalTo("ORDER"))
            .withQueryParam("sourceId", equalTo("456"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"code\":200,\"message\":\"success\"}")));

        // 执行调用
        ResultInfo<Void> result = volunteerServiceClient.deductPoints(123L, 100, "ORDER", 456L);

        // 验证
        assertNotNull(result);
        assertEquals(200, result.getCode());
    }
}
```

### 7.3 端到端测试

在真实环境中部署所有服务，进行完整的业务流程测试：

1. **测试场景 1**: 用户积分充足，成功创建订单
2. **测试场景 2**: 用户积分不足，订单创建失败
3. **测试场景 3**: user-service 宕机，购物服务优雅降级
4. **测试场景 4**: 并发下单，验证积分扣减的原子性

---

## 8. 部署指南

### 8.1 数据库准备

确保 volunteer 表包含 activity_point 字段：

```sql
-- 如果不存在，添加字段
ALTER TABLE volunteer ADD COLUMN IF NOT EXISTS activity_point INT DEFAULT 0 COMMENT '积分余额';

-- 初始化现有用户的积分
UPDATE volunteer SET activity_point = 0 WHERE activity_point IS NULL;
```

### 8.2 服务启动顺序

1. 启动 Nacos 注册中心
2. 启动 user-service（提供积分服务）
3. 启动 shopping-service（依赖积分服务）
4. 启动 gateway（统一入口）

### 8.3 配置检查清单

**shopping-service**:
- [ ] 添加 spring-cloud-starter-openfeign 依赖
- [ ] 添加 @EnableFeignClients 注解
- [ ] 配置 Feign 超时参数
- [ ] 配置 user-service 服务地址（通过 Nacos）

**user-service**:
- [ ] 添加积分查询接口
- [ ] 添加积分扣减接口
- [ ] 添加积分增加接口
- [ ] 配置日志级别

---

## 9. 监控与日志

### 9.1 关键日志

**shopping-service**:
```java
// 查询积分前
log.info("开始查询积分余额：userId={}", userId);

// 查询积分后
log.info("积分余额查询成功：userId={}, balance={}", userId, balance);

// 扣减积分前
log.info("开始扣减积分：userId={}, points={}, sourceType={}, sourceId={}", 
    userId, points, sourceType, sourceId);

// 扣减积分后
log.info("积分扣减成功：userId={}, 扣减={}, 剩余={}", userId, points, remainingPoints);
```

**user-service**:
```java
// 接收积分查询请求
log.info("接收到积分查询请求：userId={}", userId);

// 接收积分扣减请求
log.info("接收到积分扣减请求：userId={}, points={}", userId, points);

// 积分扣减成功
log.info("积分扣减成功：userId={}, 原积分={}, 扣减={}, 新积分={}", 
    userId, currentPoints, points, newPoints);
```

### 9.2 监控指标

建议添加以下监控指标：

1. **积分查询接口**:
   - QPS（每秒查询率）
   - 平均响应时间
   - 错误率

2. **积分扣减接口**:
   - QPS
   - 平均响应时间
   - 成功率
   - 积分扣减总量

3. **Feign 客户端**:
   - 调用成功率
   - 调用延迟分布
   - 熔断器状态（如使用）

---

## 10. 安全考虑

### 10.1 权限控制

- 所有积分相关接口通过 Gateway 统一鉴权
- 用户只能查询和操作自己的积分
- 管理员可查看任意用户积分（需额外权限）

### 10.2 防刷限制

- 单用户每日积分扣减次数限制（可选）
- 单用户单次订单积分扣减上限（可选）
- 异常积分变动频率监控和告警

### 10.3 审计日志

建议记录以下审计信息：

```java
// 积分变动审计日志
log.info("AUDIT: 积分变动 - userId={}, 变动类型={}, 变动金额={}, 变动前余额={}, 变动后余额={}, 来源类型={}, 来源 ID={}, 操作人={}",
    userId, changeType, points, balanceBefore, balanceAfter, sourceType, sourceId, operator);
```

---

## 11. 回滚方案

### 11.1 代码回滚

如果部署后发现问题，需要回滚：

1. 恢复 OrderServiceImpl 到修改前版本（使用临时方法）
2. 移除 VolunteerController 中的新增接口
3. 移除 Feign 相关依赖和配置

### 11.2 数据修复

如果积分数据出现问题：

```sql
-- 查看积分变动历史（如有）
SELECT * FROM point_history WHERE user_id = ? ORDER BY create_time DESC;

-- 手动修复积分余额
UPDATE volunteer SET activity_point = ? WHERE user_id = ?;

-- 记录修复操作
INSERT INTO point_history (user_id, change_type, change_amount, balance_before, balance_after, description, create_time)
VALUES (?, 'MANUAL_FIX', ?, ?, ?, '人工修复积分', NOW());
```

---

## 12. 后续优化建议

### 12.1 性能优化

1. **缓存积分数据**: 使用 Redis 缓存用户积分，减少数据库查询
2. **异步扣减**: 对于非实时场景，可使用消息队列异步扣减积分
3. **批量查询**: 支持批量查询多个用户的积分余额

### 12.2 功能扩展

1. **积分有效期**: 支持积分过期机制
2. **积分冻结**: 订单支付期间冻结相应积分
3. **积分返还**: 订单取消时自动返还积分
4. **积分明细**: 实现积分变动历史记录功能

### 12.3 可靠性提升

1. **分布式事务**: 引入 Seata 保证跨服务事务一致性
2. **幂等性保证**: 积分扣减接口支持幂等操作
3. **补偿机制**: 积分扣减失败时的补偿策略

---

## 13. 附录

### 13.1 相关文件清单

**需要新建的文件**:
- `shopping-service/src/main/java/com/hutb/shopping/client/VolunteerServiceClient.java`
- `shopping-service/src/test/java/com/hutb/shopping/client/VolunteerServiceClientIntegrationTest.java`

**需要修改的文件**:
- `user-service/src/main/java/com/hutb/user/controller/VolunteerController.java`
- `shopping-service/src/main/java/com/hutb/shopping/service/impl/OrderServiceImpl.java`
- `shopping-service/src/main/java/com/hutb/shopping/ShoppingApplication.java`
- `shopping-service/pom.xml`
- `shopping-service/src/main/resources/application.yaml`

### 13.2 参考文档

- [Spring Cloud OpenFeign 官方文档](https://spring.io/projects/spring-cloud-openfeign)
- [Feign GitHub Repository](https://github.com/OpenFeign/feign)
- [Spring Cloud Netflix Ribbon](https://spring.io/projects/spring-cloud-netflix)

### 13.3 术语表

| 术语 | 说明 |
|------|------|
| Feign | Spring Cloud 提供的声明式 Web Service 客户端 |
| ResultInfo | 统一的 REST API 响应封装类 |
| CommonException | 自定义业务异常类 |
| Gateway | API 网关服务，负责路由转发和鉴权 |
| Nacos | Alibaba 提供的服务注册与发现组件 |

---

## 14. 变更历史

| 版本 | 日期 | 作者 | 变更描述 |
|------|------|------|----------|
| v1.0 | 2026-03-08 | AI Assistant | 初始版本创建 |
