package com.hutb.commonUtils.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 流浪动物救助信息
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class PetRescuerInfo {
    // 救助地点
    private String rescueLocation;

    // 救助时间
    private Date rescueTime;

    // 救助人/发现人ID（关联用户表，可空）
    private Long rescuerId;

    // 关联pet表信息
    private Long petId;

    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String modifiedUser;
}
