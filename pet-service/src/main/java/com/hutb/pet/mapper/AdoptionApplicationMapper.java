package com.hutb.pet.mapper;

import com.hutb.pet.model.DTO.PageQueryListDTO;
import com.hutb.pet.model.pojo.AdoptionApplication;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AdoptionApplicationMapper {
    
    /**
     * 创建领养申请
     */
    @Insert("INSERT INTO adoption_application (pet_id, user_id, status, applicant_name, " +
            "applicant_phone, applicant_address, application_reason, create_time, update_time, create_user, update_user) " +
            "VALUES (#{petId}, #{userId}, #{status}, #{applicantName}, #{applicantPhone}, " +
            "#{applicantAddress}, #{applicationReason}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    int createAdoptionApplication(AdoptionApplication application);
    
    /**
     * 根据ID获取领养申请
     */
    @Select("SELECT * FROM adoption_application WHERE id = #{id}")
    AdoptionApplication getAdoptionApplicationById(Long id);
    
    /**
     * 根据宠物ID获取所有申请记录
     */
    @Select("SELECT * FROM adoption_application WHERE pet_id = #{petId}")
    List<AdoptionApplication> getAdoptionApplicationsByPetId(Long petId);
    
    /**
     * 获取所有领养申请（分页）
     */
    List<AdoptionApplication> getAllAdoptionApplications(PageQueryListDTO queryDTO);
    
    /**
     * 更新领养申请状态
     */
    @Update("UPDATE adoption_application SET status = #{status}, update_time = now(), update_user = #{updateUser} WHERE id = #{id}")
    int updateAdoptionApplicationStatus(Long id,String status,String updateUser);
    
    /**
     * 检查用户是否已对某宠物提交申请
     */
    @Select("SELECT COUNT(*) FROM adoption_application WHERE pet_id = #{petId} AND user_id = #{userId} AND status !=#{status}")
    int checkExistingApplication(Long petId,Long userId, String status);

    /**
     * 批量拒绝其他申请
     */
    @Update("UPDATE adoption_application SET status = #{status}, update_time = now(), update_user = #{updateUser} WHERE pet_id = #{petId} AND id != #{applicationId}")
    int batchRejectOtherApplications(Long applicationId,String status,String updateUser);
}