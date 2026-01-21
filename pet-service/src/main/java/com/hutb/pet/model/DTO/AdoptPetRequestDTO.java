package com.hutb.pet.model.DTO;

import lombok.Data;

@Data
public class AdoptPetRequestDTO {
    private Long petId;                 // 宠物ID
    private String applicantPhone;      // 申请人电话
    private String applicantAddress;    // 申请人地址
    private String applicationReason;   // 申请理由
}