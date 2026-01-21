package com.hutb.pet.constant;

/**
 * 宠物常量类
 */
public class PetConstant {
    
    // 领养状态常量
    public static final String ADOPTION_STATUS_AVAILABLE = "0";   // 待领养
    public static final String ADOPTION_STATUS_APPLIED = "1";     // 已申请
    public static final String ADOPTION_STATUS_ADOPTED = "2";     // 已领养
    
    // 绝育状态常量
    public static final String NEUTERED_NO = "0";    // 未绝育
    public static final String NEUTERED_YES = "1";   // 已绝育
    
    // 疫苗状态常量
    public static final String VACCINATED_NO = "0";    // 未接种
    public static final String VACCINATED_YES = "1";   // 已接种
    
    // 性别常量
    public static final String GENDER_FEMALE = "0";    // 雌性
    public static final String GENDER_MALE = "1";      // 雄性
    
    // 宠物状态常量
    public static final String PET_STATUS_NORMAL = "1";      // 正常
    public static final String PET_STATUS_ADOPTED = "2";     // 已被领养
    public static final String PET_STATUS_DELETED = "-1";     // 删除
    
    // 宠物类型常量
    public static final String SPECIES_DOG = "DOG";         // 狗
    public static final String SPECIES_CAT = "CAT";         // 猫
    public static final String SPECIES_OTHER = "OTHER";     // 其他
}