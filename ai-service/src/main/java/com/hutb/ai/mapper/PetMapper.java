package com.hutb.ai.mapper;

import com.hutb.ai.model.Pet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PetMapper {

    /**
     * 查询所有活跃的宠物列表（用于 AI 对话上下文）
     */
    @Select("select id, name, species, breed, age, gender, health_status, is_neutered, is_vaccinated, adoption_status, description, owner_id, status from pet where status = '1'")
    List<Pet> queryActivePets();
}
