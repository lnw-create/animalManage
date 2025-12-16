package com.hutb.commonUtils.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询参数
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageQueryListDTO {
    private String username;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

