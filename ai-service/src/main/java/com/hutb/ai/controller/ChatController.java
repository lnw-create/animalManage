package com.hutb.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 文字聊天
     * @param prompt
     * @return
     */
    @PostMapping("/chat")
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
     * 文字+图片聊天
     * @param prompt
     * @param image
     * @return
     */
    @PostMapping("/chat-with-image")
    public String chatWithImage(@RequestParam String prompt,
                                @RequestParam String image) {
        try {

            // 构造包含图片的消息
            return chatClient.prompt()
                    .user(prompt)
                    .user(image)
                    .call()
                    .content();
        } catch (Exception e) {
            throw new RuntimeException("处理图片时发生错误", e);
        }
    }
}
