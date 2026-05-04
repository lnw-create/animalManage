package com.hutb.pet.scheduler;

import com.hutb.pet.service.PetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宠物回访 AI 分析定时任务调度器
 */
@Component
@Slf4j
public class PetVisitAnalysisScheduler {
    
    @Autowired
    private PetService petService;
    
    /**
     * 每分钟执行一次，分析昨天的宠物回访记录
     * Cron 表达式：秒 分 时 日 月 周
     * 0 * * * * ? = 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ?")
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
