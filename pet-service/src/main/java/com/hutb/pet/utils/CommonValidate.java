package com.hutb.pet.utils;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.CommonUtils;
import com.hutb.pet.model.DTO.PetDTO;

public class CommonValidate {
    
    /**
     * 验证宠物信息
     * @param petDTO 宠物信息
     */
    public static void validatePet(PetDTO petDTO) {
        if (petDTO == null) {
            throw new CommonException("宠物信息不能为空");
        }
        if (CommonUtils.stringIsBlank(petDTO.getName())) {
            throw new CommonException("宠物名称不能为空");
        }
        if (petDTO.getName().length() > 100) {
            throw new CommonException("宠物名称长度不能超过100个字符");
        }
        if (CommonUtils.stringIsBlank(petDTO.getSpecies())) {
            throw new CommonException("宠物种类不能为空");
        }
        if (petDTO.getSpecies().length() > 50) {
            throw new CommonException("宠物种类长度不能超过50个字符");
        }
        if (petDTO.getAge() != null && petDTO.getAge().length() > 20) {
            throw new CommonException("宠物年龄长度不能超过20个字符");
        }
        if (petDTO.getHealthStatus() != null && petDTO.getHealthStatus().length() > 100) {
            throw new CommonException("健康状况长度不能超过100个字符");
        }
        if (petDTO.getDescription() != null && petDTO.getDescription().length() > 1000) {
            throw new CommonException("宠物描述长度不能超过1000个字符");
        }
        if (petDTO.getPhoto() != null && petDTO.getPhoto().length() > 500) {
            throw new CommonException("照片URL长度不能超过500个字符");
        }
        if (petDTO.getGender() != null && !("0".equals(petDTO.getGender()) || "1".equals(petDTO.getGender()))) {
            throw new CommonException("宠物性别不能为空");
        }
        if (petDTO.getIsNeutered() != null && !("0".equals(petDTO.getIsNeutered()) || "1".equals(petDTO.getIsNeutered()))) {
            throw new CommonException("绝育状态不能为空");
        }
        if (petDTO.getIsVaccinated() != null && !("0".equals(petDTO.getIsVaccinated()) || "1".equals(petDTO.getIsVaccinated()))) {
            throw new CommonException("疫苗状态不能为空");
        }
        if (petDTO.getAdoptionStatus() != null && (Integer.parseInt(petDTO.getAdoptionStatus()) < 0 || Integer.parseInt(petDTO.getAdoptionStatus()) > 2)) {
            throw new CommonException("领养状态不能为空");
        }
        if (petDTO.getOwnerId() != null && petDTO.getOwnerId() <= 0) {
            throw new CommonException("主人ID必须大于0");
        }
    }
}