package com.hutb.user.model.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 宠物/流浪动物基本信息
 */
public class Pet {
    private Long id;

    /* -------------------- 基本信息 -------------------- */
    // 宠物昵称
    private String name;

    // 动物类别：DOG、CAT、OTHER
    private String species;

    // 品种
    private String breed;

    // 性别：M-公，F-母，U-未知
    private String gender;

    // 体重(kg)
    private BigDecimal weight;

    // 是否绝育：0-未绝育，1-已绝育，2-未知
    private String sterilized;

    //是否驱虫：0-未驱虫，1-已驱虫，2-未知
    private String isDewormed;

    // 是否接种疫苗：0-未接种，1-部分接种，2-完成接种，3-未知
    private String isVaccinated;

    // 照片
    private String photo;

    // 健康状况简述：HEALTHY、SLIGHT、SERIOUS、CRITICAL
    private String healthStatus;

    // 领养建议（文字版，运营填写）
    private String adoptionTips;

    /**
     * 宠物当前状态：
     * RESCUE-救助中
     * AVAILABLE-待领养
     * RESERVED-已预约
     * ADOPTED-已领养
     * FOSTER-寄养中
     * LOST-走失
     * DECEASED-死亡
     */
    private String status;

    // 领养人ID（已领养时关联用户表）
    private Long adopterId;

    // 领养时间
    private Date adoptTime;

    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String modifiedUser;
}
