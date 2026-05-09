package com.hutb.ai.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话列表返回 VO
 */
@Data
public class ChatSessionVO {
    private String sessionId;
    private String title;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
