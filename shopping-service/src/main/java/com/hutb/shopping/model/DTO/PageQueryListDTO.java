package com.hutb.shopping.model.DTO;

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
    private String productName;
    private String status;
    private Double minPrice;
    private Double maxPrice;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

