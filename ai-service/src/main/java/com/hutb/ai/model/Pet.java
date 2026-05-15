package com.hutb.ai.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Pet {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private String age;
    private String gender;
    private String healthStatus;
    private String isNeutered;
    private String isVaccinated;
    private String adoptionStatus;
    private String description;
    private String photo;
    private Long ownerId;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
}
