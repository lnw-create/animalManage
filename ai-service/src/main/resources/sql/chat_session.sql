-- 创建 AI 会话元数据表
CREATE TABLE IF NOT EXISTS `chat_session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `session_id` varchar(64) NOT NULL COMMENT '业务会话 ID, 对应 SPRING_AI_CHAT_MEMORY.conversation_id',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `title` varchar(100) DEFAULT NULL COMMENT '会话标题（首句自动截 20 字，应用层默认为"新会话"）',
  `status` varchar(10) DEFAULT '1' COMMENT '状态：1-正常，-1-删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_id` (`session_id`),
  KEY `idx_user_status_update` (`user_id`, `status`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 会话元数据表';
