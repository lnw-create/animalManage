# 宠物领养申请功能规格说明

## 1. 需求概述

### 1.1 功能背景
在现有的宠物管理系统中，需要增加宠物领养申请功能，允许用户提交领养申请，并由管理员进行审批。

### 1.2 目标
- 实现用户提交宠物领养申请
- 支持管理员审批领养申请
- 更新宠物状态以反映领养情况

## 2. 设计方案

### 2.1 数据模型设计

#### 2.1.1 领养申请实体 (AdoptionApplication)

```java
package com.hutb.pet.model.pojo;

import lombok.Data;

import java.time.LocalDateTime;

public class AdoptionApplication {
   private Long id;                           // 申请ID
   private Long petId;                        // 宠物ID
   private Long userId;                       // 申请人ID
   private String status;                     // 申请状态: PENDING(待审批), APPROVED(已批准), REJECTED(已拒绝)
   private String applicantName;              // 申请人姓名
   private String applicantPhone;             // 申请人电话
   private String applicantAddress;           // 申请人地址
   private String applicationReason;          // 申请理由
   private Data createTime;          // 创建时间
   private D updateTime;          // 更新时间
   private String createUser;                 // 创建人
   private String updateUser;                 // 更新人

   // getter 和 setter 方法
}
```

#### 2.1.2 领养申请DTO (AdoptionApplicationDTO)
```java
package com.hutb.pet.model.DTO;

public class AdoptionApplicationDTO {
    private Long petId;                 // 宠物ID
    private Long userId;                // 申请人ID
    private String applicantName;       // 申请人姓名
    private String applicantPhone;      // 申请人电话
    private String applicantAddress;    // 申请人地址
    private String applicationReason;   // 申请理由
    
    // getter 和 setter 方法
}
```

### 2.2 接口设计

#### 2.2.1 PetService 接口扩展
```java
public interface PetService {
    // ... 现有方法
    
    /**
     * 领养宠物（提交领养申请，替代原有方法）
     * @param id 宠物id
     * @param applicationDTO 领养申请信息
     */
    void adoptPet(Long id, AdoptionApplicationDTO applicationDTO);
    
    /**
     * 获取领养申请详情
     */
    AdoptionApplication getAdoptionApplicationById(Long id);
    
    /**
     * 获取宠物的所有申请记录
     */
    List<AdoptionApplication> getAdoptionApplicationsByPetId(Long petId);
    
    /**
     * 获取所有领养申请（供管理员使用）
     */
    PageInfo getAllAdoptionApplications(PageQueryListDTO queryDTO);
    
    /**
     * 审批领养申请
     */
    void approveAdoptionApplication(Long applicationId, Boolean approved);
}
```

#### 2.2.2 PetController 接口扩展
```java
@RestController
@RequestMapping("pet")
public class PetController {
    // ... 现有方法
    
    /**
     * 领养宠物（提交领养申请）
     */
    @PostMapping("adoptPet")
    public ResultInfo adoptPet(@RequestParam Long id, @RequestBody AdoptionApplicationDTO applicationDTO) {
        try {
            petService.adoptPet(id, applicationDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 获取领养申请详情
     */
    @GetMapping("getAdoptionApplication/{id}")
    public ResultInfo getAdoptionApplication(@PathVariable Long id) {
        try {
            return ResultInfo.success(petService.getAdoptionApplicationById(id));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 获取宠物的所有申请记录
     */
    @GetMapping("getAdoptionApplicationsByPetId/{petId}")
    public ResultInfo getAdoptionApplicationsByPetId(@PathVariable Long petId) {
        try {
            return ResultInfo.success(petService.getAdoptionApplicationsByPetId(petId));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 获取所有领养申请（供管理员使用）
     */
    @PostMapping("getAllAdoptionApplications")
    public ResultInfo getAllAdoptionApplications(@RequestBody PageQueryListDTO queryDTO) {
        try {
            return ResultInfo.success(petService.getAllAdoptionApplications(queryDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 审批领养申请
     */
    @PostMapping("approveAdoptionApplication")
    public ResultInfo approveAdoptionApplication(
            @RequestParam Long applicationId, 
            @RequestParam Boolean approved) {
        try {
            petService.approveAdoptionApplication(applicationId, approved);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
}
```

### 2.3 数据库设计

#### 2.3.1 adoption_application 表结构
```sql
CREATE TABLE adoption_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '申请状态: PENDING(待审批), APPROVED(已批准), REJECTED(已拒绝)',
    applicant_name VARCHAR(100) COMMENT '申请人姓名',
    applicant_phone VARCHAR(20) COMMENT '申请人电话',
    applicant_address TEXT COMMENT '申请人地址',
    application_reason TEXT COMMENT '申请理由',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user VARCHAR(50) COMMENT '创建人',
    update_user VARCHAR(50) COMMENT '更新人',
    INDEX idx_pet_id (pet_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) COMMENT='领养申请表';
```

### 2.4 业务流程

#### 2.4.1 提交流程
1. 用户通过前端提交领养申请信息
2. 调用 pet-service 的 adoptPet 接口
3. 验证宠物是否可领养
4. 创建领养申请记录
5. 更新宠物状态为"已申请"

#### 2.4.2 审批流程
1. 管理员通过前端查看待审批的申请
2. 调用 pet-service 的 approveAdoptionApplication 接口
3. 如果审批通过：
   - 更新申请状态为"APPROVED"
   - 更新宠物状态为"已领养"
   - 记录领养人信息到宠物表
4. 如果审批拒绝：
   - 更新申请状态为"REJECTED"
   - 宠物状态恢复为"可领养"

## 3. 实现细节

### 3.1 常量定义
在 PetConstant 中添加新的状态常量：
```java
// 领养申请状态常量
public static final String ADOPTION_STATUS_PENDING = "PENDING";   // 待审批
public static final String ADOPTION_STATUS_APPROVED = "APPROVED"; // 已批准
public static final String ADOPTION_STATUS_REJECTED = "REJECTED"; // 已拒绝
```

### 3.2 参数校验
使用现有的 CommonValidate 工具类对申请信息进行校验。

### 3.3 服务实现
在 PetServiceImpl 中修改 adoptPet 方法（替换原有单参数方法），实现完整的领养申请流程：
1. 验证宠物是否存在且可领养
2. 根据传入的applicationDTO创建领养申请记录
3. 更新宠物状态为"已申请"或"已领养"（取决于审批流程）
4. 记录操作日志

### 3.4 异常处理
- 宠物不存在或不可领养时抛出 CommonException
- 重复申请同一宠物时抛出 CommonException
- 无效的申请参数时抛出 CommonException

## 4. 测试要点

### 4.1 单元测试
- 领养申请提交功能测试
- 申请审批功能测试
- 宠物状态更新测试
- 参数校验测试

### 4.2 集成测试
- 完整的申请-审批流程测试
- 并发申请处理测试
- 数据一致性验证