package com.hutb.volunteerActivity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.hutb.volunteerActivity", "com.hutb.commonUtils"})
@EnableFeignClients(basePackages = "com.hutb.volunteerActivity.client")
public class VolunteerActivityApplication {
    public static void main(String[] args) {
        SpringApplication.run(VolunteerActivityApplication.class, args);
    }
}