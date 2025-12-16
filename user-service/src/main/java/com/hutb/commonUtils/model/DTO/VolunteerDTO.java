package com.hutb.commonUtils.model.DTO;

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
public class VolunteerDTO {
    private Long id;

    private Long userId;

    private String realName;

    //性别 1:男 2:女
    private String gender;

    //身份证号（脱敏存储）
    private String idCard;

    //手机号（与用户表可相同，冗余方便联系）
    private String phone;

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
    private String modifiedUser;
}
