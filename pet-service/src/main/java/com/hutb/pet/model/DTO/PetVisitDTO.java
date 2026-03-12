package com.hutb.pet.model.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PetVisitDTO {
    private Long id;
    private Long petId;
    private String visitInfo;
    private LocalDateTime visitTime;
    // ai分析，1-正面，2-负面
    private String analysisStatus;
    // ai分析结果
    private String analysisResult;
}
