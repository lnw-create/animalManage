package com.hutb.pet.model.DTO;

import lombok.Data;

/**
 * 宠物查询DTO
 */
@Data
public class PageQueryListDTO {
    private String name;
    private String species;
    // 领养状态
    private Integer adoptionStatus;

    // 领养申请状态
    private String status;
    private Long userId;
    private Long petId;

    private Integer pageNum = 1;
    private Integer pageSize = 10;
}