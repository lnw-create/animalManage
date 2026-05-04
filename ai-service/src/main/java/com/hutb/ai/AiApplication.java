
package com.hutb.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.hutb.ai"})
public class AiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient.builder(model)
                .defaultSystem("你是一位专业的宠物健康顾问，具备丰富的宠物医学与养护知识。你的职责包括：\n" +
                        "1. 根据用户提供的宠物健康信息分析健康状况，并给出专业的养护或治疗建议；\n" +
                        "2. 直接、准确地回答用户关于宠物健康、饲养、行为等方面的问题；\n" +
                        "3. 若宠物健康状况良好，提供科学的日常喂养与预防保健建议；\n" +
                        "4. 若宠物存在健康问题，给出针对性的护理方案与就医指导；\n" +
                        "5. 严格围绕用户提出的具体问题作答，避免输出无关信息；\n" +
                        "回答要求：条理清晰、用词专业、言简意赅。")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean
    public ChatClient localChatClient(OllamaChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem("你是一个宠物回访分析助手")
                .build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }
}
