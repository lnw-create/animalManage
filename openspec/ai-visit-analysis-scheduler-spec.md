# AI 分析宠物回访定时任务变更

## 变更概述

将 `aiAnalyzePetVisit` 方法从手动调用的 REST API 端点改为每日定时执行的任务，自动分析前一天的宠物回访记录。

## 变更目标

1. 移除现有的 REST API 端点 `/pet/aiAnalyzePetVisit`
2. 创建定时任务，每天自动执行一次
3. 根据时间筛选出昨天的回访数据并进行批量分析
4. 保留原有的 AI 分析逻辑，但改为批量处理模式

## 当前实现

### 现有代码位置
- **Controller**: `PetController.aiAnalyzePetVisit()` (L229-L239)
- **Service**: `PetServiceImpl.aiAnalyzePetVisit()` (L367-L403)
- **功能**: 接收单个 `visitId` 参数，分析单条回访记录

### 现有逻辑流程
1. 根据 ID 查询回访记录
2. 构造 AI 提示词
3. 调用 AI 服务分析
4. 更新数据库中的分析状态和结果

## 变更方案

### 1. 启用 Spring 定时任务支持

在 `PetApplication.java` 中添加 `@EnableScheduling` 注解：

```java
@SpringBootApplication(scanBasePackages = {"com.hutb.pet", "com.hutb.commonUtils"})
@EnableFeignClients(basePackages = {"com.hutb.pet.client"})
@EnableScheduling  // 新增：启用定时任务
public class PetApplication {
    // ...
}
```

### 2. 创建定时任务调度器

在 `scheduler` 包下创建新的调度类 `PetVisitAnalysisScheduler.java`：

```java
package com.hutb.pet.scheduler;

import com.hutb.pet.service.PetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Slf4j
public class PetVisitAnalysisScheduler {
    
    @Autowired
    private PetService petService;
    
    /**
     * 每日凌晨 2 点执行，分析昨天的宠物回访记录
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyPetVisitAnalysis() {
        log.info("开始执行每日宠物回访 AI 分析定时任务");
        
        try {
            // 计算昨天的日期范围
            LocalDateTime startTime = LocalDate.now().minusDays(1).atStartOfDay();
            LocalDateTime endTime = startTime.plusDays(1);
            
            log.info("分析日期范围：{} 至 {}", startTime, endTime);
            
            // 调用 service 层进行批量分析
            petService.analyzePetVisitsByDateRange(startTime, endTime);
            
            log.info("每日宠物回访 AI 分析定时任务执行完成");
        } catch (Exception e) {
            log.error("每日宠物回访 AI 分析定时任务执行失败", e);
        }
    }
}
```

### 3. 扩展 Service 层接口

在 `PetService.java` 接口中添加新方法：

```java
/**
 * 根据日期范围批量分析宠物回访记录
 * @param startTime 开始时间
 * @param endTime 结束时间
 */
void analyzePetVisitsByDateRange(LocalDateTime startTime, LocalDateTime endTime);
```

### 4. 实现 Service 层业务逻辑

在 `PetServiceImpl.java` 中实现新方法：

```java
@Override
public void analyzePetVisitsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
    log.info("开始批量分析宠物回访记录，时间范围：{} 至 {}", startTime, endTime);
    
    try {
        // 1. 查询指定时间范围内的所有未分析的回访记录
        List<PetVisitDTO> visits = petMapper.queryPetVisitsByDateRange(startTime, endTime);
        
        if (visits == null || visits.isEmpty()) {
            log.info("在指定时间范围内没有找到需要分析的回访记录");
            return;
        }
        
        log.info("找到 {} 条需要分析的回访记录", visits.size());
        
        // 2. 逐条分析
        int successCount = 0;
        int failCount = 0;
        
        for (PetVisitDTO visit : visits) {
            try {
                analyzeSingleVisit(visit);
                successCount++;
            } catch (Exception e) {
                log.error("分析回访记录失败，ID: {}", visit.getId(), e);
                failCount++;
            }
        }
        
        log.info("批量分析完成，成功：{}, 失败：{}", successCount, failCount);
        
    } catch (Exception e) {
        log.error("批量分析宠物回访记录失败", e);
        throw new CommonException("批量分析失败：" + e.getMessage());
    }
}

/**
 * 分析单条回访记录（提取原有逻辑）
 */
private void analyzeSingleVisit(PetVisitDTO visit) {
    log.info("开始 AI 分析宠物回访信息，ID: {}", visit.getId());
    
    // 2. 构造提示词
    String prompt = "请分析以下宠物回访信息，判断此次回访的状态是正面还是负面。\n" +
                   "请按以下 JSON 格式返回：{\"status\": \"positive 或 negative\", \"result\": \"详细分析结果\"}\n" +
                   "回访信息：" + visit.getVisitInfo();
    
    // 3. 调用 AI 服务进行分析
    AIAnalysisResponse response = aiChatClient.analyzeVisit(prompt);
    
    // 4. 更新数据库
    visit.setAnalysisStatus(response.getStatus());
    visit.setAnalysisResult(response.getResult());
    
    int result = petMapper.aiAnalysisPetVisit(visit);
    if (result == 0) {
        throw new CommonException("更新 AI 分析结果失败");
    }
    
    log.info("AI 分析宠物回访信息完成，ID: {}, 状态：{}, 结果：{}", 
            visit.getId(), response.getStatus(), response.getResult());
}
```

### 5. 添加 Mapper 查询方法

在 `PetMapper.java` 接口中添加按日期范围查询的方法：

```java
/**
 * 根据日期范围查询回访记录（只查询未分析的记录）
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @return 回访记录列表
 */
@Select("<script>" +
        "SELECT * FROM pet_visit " +
        "WHERE visit_time >= #{startTime} " +
        "AND visit_time < #{endTime} " +
        "AND (analysis_status IS NULL OR analysis_status = '') " +
        "ORDER BY id" +
        "</script>")
List<PetVisitDTO> queryPetVisitsByDateRange(@Param("startTime") LocalDateTime startTime, 
                                            @Param("endTime") LocalDateTime endTime);
```

### 6. 删除或废弃原有 Controller 端点

**选项 A：直接删除（推荐）**
删除 `PetController.java` 中的 `aiAnalyzePetVisit` 方法（L226-L239）

**选项 B：标记为废弃（过渡方案）**
```java
@Deprecated
@PostMapping("aiAnalyzePetVisit")
public ResultInfo aiAnalyzePetVisit(@RequestParam Long visitId) {
    // ... 原有逻辑
}
```

## 数据库变更

无需修改数据库表结构，但需要确保 `pet_visit` 表的 `visit_time` 字段有索引以提升查询性能：

```sql
CREATE INDEX idx_visit_time ON pet_visit(visit_time);
```

## 配置要求

### application.yaml 配置

确保应用配置中包含定时任务相关配置（可选，使用默认值）：

```yaml
spring:
  task:
    scheduling:
      pool:
        size: 5  # 定时任务线程池大小
      thread-name-prefix: scheduled-task-
```

## Cron 表达式说明

- `0 0 2 * * ?` - 每天凌晨 2 点执行
- 可根据实际需求调整执行时间
- 建议避开业务高峰期和数据备份时间

## 错误处理策略

1. **单次分析失败**：记录错误日志，继续处理下一条记录
2. **整体任务失败**：捕获异常并记录日志，不影响下次定时任务执行
3. **重试机制**：当前版本不支持重试，失败记录需等待下次定时任务或通过手动方式补分析

## 监控与日志

关键日志点：
1. 定时任务启动时记录开始日志
2. 查询到待分析记录数量时记录统计信息
3. 每条记录分析完成时记录进度
4. 批量分析完成后汇总成功/失败数量
5. 异常情况记录详细错误信息

## 测试建议

1. **单元测试**：
   - 测试日期范围计算逻辑
   - 测试空数据场景
   - 测试单条记录分析失败场景
   
2. **集成测试**：
   - 创建测试数据验证定时任务触发
   - 验证批量分析结果正确性
   - 验证错误处理和日志输出

3. **手动测试**：
   - 临时修改 cron 表达式为每分钟执行进行测试
   - 验证生产环境部署后的首次执行情况

## 部署注意事项

1. **多实例部署**：如果应用部署多个实例，需要配置分布式锁避免重复执行
2. **时区设置**：确保服务器时区与业务需求一致
3. **监控告警**：建议配置定时任务执行失败的告警通知

## 后续优化方向

1. 增加批量分析的重试机制
2. 添加分析进度持久化，支持断点续分析
3. 增加分布式锁支持，避免多实例重复执行
4. 提供手动触发接口用于补分析历史数据
5. 增加统计分析功能，展示分析结果概览

## 变更影响评估

### 优点
- 自动化执行，减少人工操作
- 统一分析时间，便于数据统计
- 降低忘记分析的风险

### 潜在风险
- 失去实时分析能力（可通过保留手动触发接口解决）
- 大批量数据处理时可能耗时较长
- 多实例部署时需要额外的分布式锁机制

### 兼容性
- **破坏性变更**：移除了原有的 REST API 端点
- 如前端或其他服务依赖该接口，需要提前沟通并提供替代方案
