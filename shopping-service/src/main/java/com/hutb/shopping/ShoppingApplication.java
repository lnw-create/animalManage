package com.hutb.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hutb.shopping", "com.hutb.commonUtils"})
public class ShoppingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingApplication.class, args);
    }
}
