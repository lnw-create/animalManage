package com.hutb.ai.service;

import com.hutb.ai.model.ChatSession;
import com.hutb.ai.model.ChatSessionVO;

import java.util.List;
import java.util.Map;

public interface ChatSessionService {

    /** 为当前用户新建一个会话，返回 sessionId 和默认 title */
    Map<String, String> createSession();

    /** 当前用户的会话列表（按 update_time DESC） */
    List<ChatSessionVO> listMySessions();

    /** 重命名会话（校验归属） */
    void renameSession(String sessionId, String title);

    /** 删除会话（逻辑删 + 清空消息体） */
    void deleteSession(String sessionId);

    /** 取会话历史消息（校验归属） */
    List<Map<String, String>> getMessages(String sessionId);

    /**
     * 流式聊天前调用：
     * 1. 校验归属（不属于当前用户则抛异常）
     * 2. 若 title 仍是默认值“新会话”，截 firstUserMsg 前 20 字回写
     * 3. 否则只 bump update_time
     */
    void touchSession(String sessionId, String firstUserMsg);

    /** 内部：校验归属，找不到或非当前用户抛异常，返回会话 */
    ChatSession assertOwnership(String sessionId);
}
