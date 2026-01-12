package com.hutb.volunteerActivity.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 志愿活动
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VolunteerActivity {
    private Long id;
    
    private String activityName;
    
    private String description;
    
    private Date startTime;
    
    private Date endTime;
    
    private String location;

    // 活动最大参与人数
    private Integer maxParticipants;

    // 当前参与人数
    private Integer currentParticipants = 0;

    //志愿时长
    private Double volunteerHours;
    
    // 活动状态：1-正常 0-暂停 -1-结束
    private String status = "1";
    
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String modifiedUser;
}