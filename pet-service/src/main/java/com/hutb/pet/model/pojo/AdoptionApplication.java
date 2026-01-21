package com.hutb.pet.model.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdoptionApplication {
    private Long id;                           // 申请ID
    private Long petId;                        // 宠物ID
    private Long userId;                       // 申请人ID
    private String status;                     // 申请状态: PENDING(待审批), APPROVED(已批准), REJECTED(已拒绝)
    private String applicantName;              // 申请人姓名
    private String applicantPhone;             // 申请人电话
    private String applicantAddress;           // 申请人地址
    private String applicationReason;          // 申请理由
    private LocalDateTime createTime;          // 创建时间
    private LocalDateTime updateTime;          // 更新时间
    private String createUser;                 // 创建人
    private String updateUser;                 // 更新人
}