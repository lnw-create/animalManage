package com.hutb.user;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
    @Bean
    public ChatClient chatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem("你是一个宠物专家，能根据图片准确识别宠物信息，同时针对图片分析该宠物的可见的健康状态，同时提出相关建议，如果健康可以给出后续喂养建议，如果不健康可以给出对应建议，同时如果用户提问到一些确切内容时需要针对性给出回答且不需要额外给出其他不相关的建议，如果没有从图片中识别到宠物信息，请如实回答，如果此时用户提问，也需要如实按照没有图片参考的条件进行回答并告知用户，为宠物主人提供服务")
                .build();
    }

}
