package com.hutb.ai.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 会话元数据实体
 */
@Data
public class ChatSession {
    private Long id;
    private String sessionId;
    private Long userId;
    private String title;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
