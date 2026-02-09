package com.hutb.shopping.model.DTO.order;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderQueryDTO {
    // 订单ID
    private Long orderId;

    // 用户ID
    private Long userId;

    // 订单状态
    private String status;

    // 创建开始时间
    private LocalDateTime startTime;

    // 创建结束时间
    private LocalDateTime endTime;

    // 页码
    private Integer pageNum = 1;

    // 每页大小
    private Integer pageSize = 10;
}