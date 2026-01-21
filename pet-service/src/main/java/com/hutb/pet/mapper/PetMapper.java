package com.hutb.pet.mapper;

import com.hutb.pet.model.DTO.PageQueryListDTO;
import com.hutb.pet.model.DTO.PetDTO;
import com.hutb.pet.model.pojo.Pet;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PetMapper {
    
    /**
     * 新增宠物
     * @param petDTO 宠物信息
     * @return 影响行数
     */
    @Insert("insert into pet (name, species, breed, age, gender, health_status, is_neutered, is_vaccinated, adoption_status, description, photo, owner_id, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name}, #{species}, #{breed}, #{age}, #{gender}, #{healthStatus}, #{isNeutered}, #{isVaccinated}, #{adoptionStatus}, #{description}, #{photo}, #{ownerId}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    int addPet(PetDTO petDTO);

    /**
     * 根据ID删除宠物（软删除，更新状态为-1）
     * @param id 宠物ID
     * @param status 状态值
     * @param updateUser 修改人
     * @return 影响行数
     */
    @Update("UPDATE pet SET status = #{status}, update_time = now(), update_user = #{updateUser} WHERE id = #{id}")
    int removePet(long id, String status, String updateUser);

    /**
     * 更新宠物
     * @param petDTO 宠物信息
     * @return 影响行数
     */
    @Update("UPDATE pet SET name = #{name}, species = #{species}, breed = #{breed}, age = #{age}, " +
            "gender = #{gender}, health_status = #{healthStatus}, is_neutered = #{isNeutered}, is_vaccinated = #{isVaccinated}, " +
            "adoption_status = #{adoptionStatus}, description = #{description}, photo = #{photo}, owner_id = #{ownerId}, " +
            "update_time = now(), update_user = #{updateUser} WHERE id = #{id}")
    int updatePet(PetDTO petDTO);

    /**
     * 根据ID查询宠物
     * @param id 宠物ID
     * @param status 排除的状态值
     * @return 宠物信息
     */
    @Select("select * from pet where id = #{id} and status != #{status}")
    Pet queryPetById(long id, String status);

    /**
     * 查询宠物列表（分页）
     * @param petQueryDTO 分页查询参数
     * @return 宠物列表
     */
    List<Pet> queryPetList(PageQueryListDTO petQueryDTO);

    /**
     * 宠物 adoption
     * @param id 宠物ID
     * @param username 用户名
     */
    @Update("UPDATE pet SET adoption_status = #{adoptionStatus}, update_time = now(), update_user = #{username} WHERE id = #{id}")
    void adoptPet(Long id, String username, String adoptionStatus);
}