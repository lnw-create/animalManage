package com.hutb.volunteerActivity.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 志愿活动参与者
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivityParticipant {
    private Long id;
    
    private Long activityId;
    
    private Long userId;
    
    private Date joinTime;
    
    // 状态：1-正常 0-取消 -1-删除
    private String status = "1";
    
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
}
