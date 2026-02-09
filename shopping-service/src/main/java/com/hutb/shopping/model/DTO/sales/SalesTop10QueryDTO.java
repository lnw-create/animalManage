package com.hutb.shopping.model.DTO.sales;

import lombok.Data;


@Data
public class SalesTop10QueryDTO {
    // 开始时间
    private String startTime;

    // 结束时间
    private String endTime;
    
    // 限制返回数量，默认为10
    private Integer limit = 10;
}