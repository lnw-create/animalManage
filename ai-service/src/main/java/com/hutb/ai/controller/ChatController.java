package com.hutb.ai.controller;

import com.hutb.ai.mapper.PetMapper;
import com.hutb.ai.model.Pet;
import com.hutb.ai.service.ChatSessionService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatClient localChatClient;

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private PetMapper petMapper;

    public ChatController(@Qualifier("chatClient") ChatClient chatClient,
                          @Qualifier("localChatClient") ChatClient localChatClient) {
        this.chatClient = chatClient;
        this.localChatClient = localChatClient;
    }

    /**
     * 文字聊天 —— 调用远程大模型 API
     * 流式调用前先校验 sessionId 归属，并刷新会话标题/活跃时间。
     * 自动查询宠物列表作为上下文提供给 AI。
     */
    @GetMapping("allUser/chat")
    public Flux<String> chat(@RequestParam String prompt, @RequestParam String sessionId) {
        chatSessionService.touchSession(sessionId, prompt);

        List<Pet> pets = petMapper.queryActivePets();
        String petContext = buildPetContext(pets);

        return chatClient.prompt()
                .system("你是动物管理系统的智能助手。以下是当前系统中可领养的宠物信息，请基于这些信息回答用户问题：\n\n" + petContext)
                .user(prompt)
                .advisors(
                        a -> a.param(ChatMemory.CONVERSATION_ID, sessionId)
                )
                .stream()
                .content();
    }

    private String buildPetContext(List<Pet> pets) {
        if (pets == null || pets.isEmpty()) {
            return "当前系统中暂无宠物信息。";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pets.size(); i++) {
            Pet p = pets.get(i);
            sb.append(i + 1).append(". ");
            sb.append("名称:").append(p.getName())
              .append(", 种类:").append(p.getSpecies())
              .append(", 品种:").append(p.getBreed())
              .append(", 年龄:").append(p.getAge())
              .append(", 性别:").append("1".equals(p.getGender()) ? "雄性" : "雌性")
              .append(", 健康状态:").append(p.getHealthStatus())
              .append(", 绝育:").append("1".equals(p.getIsNeutered()) ? "是" : "否")
              .append(", 疫苗:").append("1".equals(p.getIsVaccinated()) ? "已接种" : "未接种")
              .append(", 领养状态:").append(adoptionStatusLabel(p.getAdoptionStatus()))
              .append(", 描述:").append(p.getDescription());
            sb.append("\n");
        }
        return sb.toString();
    }

    private String adoptionStatusLabel(String status) {
        if (status == null) return "未知";
        return switch (status) {
            case "0" -> "待领养";
            case "1" -> "已申请";
            case "2" -> "已领养";
            default -> "未知";
        };
    }

    /**
     * AI 分析宠物回访信息 —— 支持本地/远程模型切换
     * @param prompt 提示词
     * @param useLocalModel true=本地Ollama，false=远程API（默认）
     * @return 分析结果，包含 status（0=负面，1=正面）和 result
     */
    @GetMapping("/analyze-visit")
    public Map<String, String> analyzeVisit(@RequestParam String prompt,
                                            @RequestParam(defaultValue = "false") boolean useLocalModel) {
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

        ChatClient client = useLocalModel ? localChatClient : chatClient;
        String response = client.prompt()
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
