package com.hutb.pet.model.DTO;

import lombok.Data;

/**
 * 宠物信息DTO
 */
@Data
public class PetDTO {
    //宠物ID
    private Long id;

    //宠物名称
    private String name;

    //宠物种类（如狗、猫、兔子等）
    private String species;

    // 宠物品种
    private String breed;

    //宠物年龄（月龄或岁数）
    private String age;

    //宠物性别 (0-雌性, 1-雄性)
    private String gender;

    //健康状况
    private String healthStatus;

    //是否已绝育 (0-否, 1-是)
    private String isNeutered;

    //是否接种疫苗 (0-否, 1-是)
    private String isVaccinated;

    //领养状态 (0-待领养, 1-已申请, 2-已领养)
    private Integer adoptionStatus;

    //宠物描述/背景故事
    private String description;

    //主图URL
    private String photo;

    //当前主人ID（领养人）
    private Long ownerId;
}