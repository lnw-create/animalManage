package com.hutb.volunteerActivity.model.DTO;

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
    private String activityName;
    private String status;
    private String startTime;
    private String endTime;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

