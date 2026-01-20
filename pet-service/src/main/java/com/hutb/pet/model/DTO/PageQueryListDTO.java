package com.hutb.pet.model.DTO;

import lombok.Data;

/**
 * 宠物查询DTO
 */
@Data
public class PageQueryListDTO {
    private String name;
    private String species;
    private Integer adoptionStatus;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}