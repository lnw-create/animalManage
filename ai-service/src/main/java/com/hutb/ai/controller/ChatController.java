package com.hutb.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatClient localChatClient;

    public ChatController(@Qualifier("chatClient") ChatClient chatClient,
                          @Qualifier("localChatClient") ChatClient localChatClient) {
        this.chatClient = chatClient;
        this.localChatClient = localChatClient;
    }

    /**
     * 文字聊天 —— 调用远程大模型 API
     * @param prompt
     * @return
     */
    @GetMapping("allUser/chat")
    public Flux<String> chat(@RequestParam String prompt,@RequestParam String sessionId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(
                        a -> a.param(ChatMemory.CONVERSATION_ID, sessionId)
                )
                .stream()
                .content();
    }

    /**
     * AI 分析宠物回访信息 —— 调用本地 Ollama 模型
     * @param prompt 提示词
     * @return 分析结果，包含 status（0=负面，1=正面）和 result
     */
    @GetMapping("/analyze-visit")
    public Map<String, String> analyzeVisit(@RequestParam String prompt) {
        String systemPrompt = """
                你是一个宠物回访分析助手。请分析以下回访信息，判断此次回访的状态是正面还是负面。

                请按以下 JSON 格式返回：
                {
                    "status": "0 或 1",
                    "result": "详细的分析结果说明"
                }

                要求：
                1. status 只能是 "0"（负面）或 "1"（正面）
                2. result 字段提供具体的分析说明，50 字以内
                3. 只返回 JSON 格式数据，不要其他内容
                """;

        String response = localChatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .call()
                .content();

        // 解析返回的 JSON 字符串
        try {
            // 提取 JSON 部分（去除可能的 markdown 标记）
            response = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

            // 简单提取 status 和 result
            String status = extractJsonValue(response, "status");
            String result = extractJsonValue(response, "result");

            return Map.of("status", status, "result", result);
        } catch (Exception e) {
            // 如果解析失败，返回默认的负面评价
            return Map.of("status", "0", "result", "AI 响应格式解析失败：" + response);
        }
    }

    /**
     * 从 JSON 字符串中提取指定字段的值
     */
    private String extractJsonValue(String json, String key) {
        int keyStart = json.indexOf("\"" + key + "\"");
        if (keyStart == -1) return "";

        int colonIndex = json.indexOf(":", keyStart);
        if (colonIndex == -1) return "";

        int quoteStart = json.indexOf("\"", colonIndex);
        if (quoteStart == -1) return "";

        int quoteEnd = json.indexOf("\"", quoteStart + 1);
        if (quoteEnd == -1) return "";

        return json.substring(quoteStart + 1, quoteEnd);
    }
}
