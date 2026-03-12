package com.hutb.pet.model.DTO;

import lombok.Data;

/**
 * AI 分析响应 DTO
 */
@Data
public class AIAnalysisResponse {
    /**
     * 分析状态：positive（正面）或 negative（负面）
     */
    private String status;
    
    /**
     * 分析结果详情
     */
    private String result;
}
