package com.hutb.pet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.hutb.pet", "com.hutb.commonUtils"})
@EnableFeignClients(basePackages = {"com.hutb.pet.client"})
@EnableScheduling  // 启用定时任务支持
public class PetApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetApplication.class, args);
    }
}
