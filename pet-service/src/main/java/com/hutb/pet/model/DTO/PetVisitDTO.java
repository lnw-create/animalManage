package com.hutb.pet.model.DTO;

import lombok.Data;

@Data
public class PetVisitDTO {
    private Long id;
    private Long petId;
    private String visitInfo;
    private String visitTime;
}
