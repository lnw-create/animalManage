package com.hutb.ai.service.impl;

import com.hutb.ai.mapper.ChatSessionMapper;
import com.hutb.ai.model.ChatSession;
import com.hutb.ai.model.ChatSessionVO;
import com.hutb.ai.service.ChatSessionService;
import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {

    private static final String DEFAULT_TITLE = "新会话";
    private static final int TITLE_MAX_LEN = 20;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMemory chatMemory;

    @Override
    public Map<String, String> createSession() {
        Long userId = currentUserId();
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        ChatSession s = new ChatSession();
        s.setSessionId(sessionId);
        s.setUserId(userId);
        s.setTitle(DEFAULT_TITLE);
        chatSessionMapper.insert(s);
        Map<String, String> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("title", DEFAULT_TITLE);
        return result;
    }

    @Override
    public List<ChatSessionVO> listMySessions() {
        return chatSessionMapper.listByUser(currentUserId());
    }

    @Override
    public void renameSession(String sessionId, String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new CommonException("标题不能为空");
        }
        String trimmed = title.trim();
        if (trimmed.length() > TITLE_MAX_LEN * 5) {
            trimmed = trimmed.substring(0, TITLE_MAX_LEN * 5);
        }
        assertOwnership(sessionId);
        chatSessionMapper.updateTitle(sessionId, currentUserId(), trimmed);
    }

    @Override
    public void deleteSession(String sessionId) {
        assertOwnership(sessionId);
        chatSessionMapper.softDelete(sessionId, currentUserId());
        try {
            chatMemory.clear(sessionId);
        } catch (Exception e) {
            log.warn("clear chat memory for {} failed: {}", sessionId, e.getMessage());
        }
    }

    @Override
    public List<Map<String, String>> getMessages(String sessionId) {
        assertOwnership(sessionId);
        List<Message> msgs = chatMemory.get(sessionId);
        return msgs.stream().map(m -> {
            Map<String, String> mp = new HashMap<>();
            mp.put("type", convertType(m.getMessageType()));
            mp.put("content", m.getText());
            return mp;
        }).toList();
    }

    @Override
    public void touchSession(String sessionId, String firstUserMsg) {
        ChatSession s = assertOwnership(sessionId);
        if (DEFAULT_TITLE.equals(s.getTitle()) && firstUserMsg != null && !firstUserMsg.isBlank()) {
            String t = firstUserMsg.trim();
            if (t.length() > TITLE_MAX_LEN) {
                t = t.substring(0, TITLE_MAX_LEN);
            }
            chatSessionMapper.updateTitle(sessionId, s.getUserId(), t);
        } else {
            chatSessionMapper.touch(sessionId);
        }
    }

    @Override
    public ChatSession assertOwnership(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new CommonException("sessionId 不能为空");
        }
        ChatSession s = chatSessionMapper.findBySessionId(sessionId);
        if (s == null) {
            throw new CommonException("会话不存在");
        }
        Long uid = currentUserId();
        if (!s.getUserId().equals(uid)) {
            throw new CommonException("无权访问该会话");
        }
        return s;
    }

    private Long currentUserId() {
        Long uid = UserContext.getUserId();
        if (uid == null) {
            throw new CommonException("未登录");
        }
        return uid;
    }

    private String convertType(MessageType t) {
        if (t == MessageType.USER) return "user";
        if (t == MessageType.ASSISTANT) return "ai";
        return "system";
    }
}
