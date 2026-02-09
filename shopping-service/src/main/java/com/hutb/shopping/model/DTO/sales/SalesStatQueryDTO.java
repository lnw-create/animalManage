package com.hutb.shopping.model.DTO.sales;

import lombok.Data;

import java.util.Date;

/**
 * 通用销售统计查询DTO
 */
@Data
public class SalesStatQueryDTO {
    // 查询开始时间
    private Date startTime;

    // 查询结束时间
    private Date endTime;

    // 统计类型 (如: daily, weekly, monthly, top_ranking等)
    private String statType;

    // 限制返回数量 (主要用于top N查询)
    private Integer limit = 10;

    // 可选的商品分类ID
    private Long categoryId;

    // 可选的用户ID（用于特定用户的销售统计）
    private Long userId;
}