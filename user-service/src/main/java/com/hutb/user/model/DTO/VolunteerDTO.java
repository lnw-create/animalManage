package com.hutb.user.model.DTO;

import com.hutb.user.model.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 志愿者
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VolunteerDTO extends User{
    private Long userId;

    //身份证号
    private String idCard;

    //现居地址
    private String address;

    /* -------------------- 志愿业务信息 -------------------- */
    //累计志愿时长（小时）
    private Double totalHours = 0.0;

    //参与活动次数
    private Integer activityCount = 0;
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
}
