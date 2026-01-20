package com.hutb.pet.service;

import com.hutb.pet.model.DTO.PetDTO;
import com.hutb.pet.model.pojo.PageInfo;
import com.hutb.pet.model.DTO.PageQueryListDTO;

public interface PetService {
    
    /**
     * 新增宠物
     * @param petDTO 宠物信息
     */
    void addPet(PetDTO petDTO);

    /**
     * 删除宠物
     * @param id 宠物id
     */
    void removePet(Long id);

    /**
     * 更新宠物
     * @param petDTO 宠物信息
     */
    void updatePet(PetDTO petDTO);

    /**
     * 查询宠物列表
     * @param petQueryDTO 分页查询参数
     * @return 宠物列表
     */
    PageInfo queryPetList(PageQueryListDTO petQueryDTO);
}