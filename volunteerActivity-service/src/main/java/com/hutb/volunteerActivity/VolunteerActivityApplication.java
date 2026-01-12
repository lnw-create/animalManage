package com.hutb.volunteerActivity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hutb.VolunteerActivity", "com.hutb.commonUtils"})
public class VolunteerActivityApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolunteerActivityApplication.class, args);
    }
}