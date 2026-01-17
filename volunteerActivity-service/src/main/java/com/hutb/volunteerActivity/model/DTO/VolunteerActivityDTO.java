package com.hutb.volunteerActivity.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 志愿活动数据传输对象
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VolunteerActivityDTO {
    private Long id;
    
    private String activityName;
    
    private String description;
    
    private String startTime;
    
    private String endTime;
    
    private String location;

    // 活动最大参与人数
    private Integer maxParticipants;

    // 当前参与人数
    private Integer currentParticipants = 0;

    //志愿时长
    private Double volunteerHours;

    private String status = "1";
    
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
}