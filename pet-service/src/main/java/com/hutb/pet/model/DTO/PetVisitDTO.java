package com.hutb.pet.model.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PetVisitDTO {
    private Long id;
    private Long petId;
    private String visitInfo;
    private LocalDateTime visitTime;
}
