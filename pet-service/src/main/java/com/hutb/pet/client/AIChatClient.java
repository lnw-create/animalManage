package com.hutb.pet.client;

import com.hutb.pet.model.DTO.AIAnalysisResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AI 服务客户端
 */
@FeignClient(name = "ai-service")
public interface AIChatClient {
    
    /**
     * 调用 AI 对话接口进行分析
     * @param prompt 提示词
     * @return AI 分析响应
     */
    @GetMapping("/ai/analyze-visit")
    AIAnalysisResponse analyzeVisit(@RequestParam String prompt);
}
