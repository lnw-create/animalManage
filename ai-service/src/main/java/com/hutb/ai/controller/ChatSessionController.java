package com.hutb.ai.controller;

import com.hutb.ai.model.ChatSessionVO;
import com.hutb.ai.model.ResultInfo;
import com.hutb.ai.service.ChatSessionService;
import com.hutb.commonUtils.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI 会话管理（用户自己的会话）
 */
@RestController
@RequestMapping("/ai/allUser/sessions")
@Slf4j
public class ChatSessionController {

    @Autowired
    private ChatSessionService chatSessionService;

    /** 新建会话 */
    @PostMapping("")
    public ResultInfo<Map<String, String>> create() {
        try {
            return ResultInfo.success(chatSessionService.createSession());
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            log.error("createSession error", e);
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /** 我的会话列表 */
    @GetMapping("")
    public ResultInfo<List<ChatSessionVO>> list() {
        try {
            return ResultInfo.success(chatSessionService.listMySessions());
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            log.error("listSessions error", e);
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /** 重命名 */
    @PutMapping("/{sessionId}")
    public ResultInfo<Void> rename(@PathVariable String sessionId,
                                   @RequestBody Map<String, String> body) {
        try {
            chatSessionService.renameSession(sessionId, body.get("title"));
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            log.error("renameSession error", e);
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /** 删除（逻辑删 + 清空消息） */
    @DeleteMapping("/{sessionId}")
    public ResultInfo<Void> delete(@PathVariable String sessionId) {
        try {
            chatSessionService.deleteSession(sessionId);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            log.error("deleteSession error", e);
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /** 取会话历史消息 */
    @GetMapping("/{sessionId}/messages")
    public ResultInfo<List<Map<String, String>>> messages(@PathVariable String sessionId) {
        try {
            return ResultInfo.success(chatSessionService.getMessages(sessionId));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            log.error("getMessages error", e);
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
}
