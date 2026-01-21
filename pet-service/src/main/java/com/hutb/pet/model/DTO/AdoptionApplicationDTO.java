package com.hutb.pet.model.DTO;

import lombok.Data;

@Data
public class AdoptionApplicationDTO {
    private Long petId;                 // 宠物ID
    private Long userId;                // 申请人ID
    private String applicantName;       // 申请人姓名
    private String applicantPhone;      // 申请人电话
    private String applicantAddress;    // 申请人地址
    private String applicationReason;   // 申请理由
}