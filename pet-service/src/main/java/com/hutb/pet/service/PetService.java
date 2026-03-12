package com.hutb.pet.service;

import com.hutb.pet.model.DTO.*;
import com.hutb.pet.model.pojo.PageInfo;
import com.hutb.pet.model.pojo.AdoptionApplication;

import java.util.List;

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

    /**
     * 领养宠物（提交领养申请，替代原有方法）
     * @param adoptPetRequestDTO 领养宠物请求参数（用户信息从UserContext自动获取）
     */
    void adoptPet(AdoptPetRequestDTO adoptPetRequestDTO);
    
    /**
     * 获取领养申请详情
     * @param id 申请ID
     * @return 领养申请信息
     */
    AdoptionApplication getAdoptionApplicationById(Long id);
    
    /**
     * 获取宠物的所有申请记录
     * @param petId 宠物ID
     * @return 领养申请列表
     */
    java.util.List<AdoptionApplication> getAdoptionApplicationsByPetId(Long petId);
    
    /**
     * 获取所有领养申请（供管理员使用）
     * @param queryDTO 查询参数
     * @return 分页的领养申请列表
     */
    PageInfo getAllAdoptionApplications(PageQueryListDTO queryDTO);
    
    /**
     * 审批领养申请
     * @param applicationId 申请ID
     * @param approved 是否批准(true-批准，false-拒绝)
     */
    void approveAdoptionApplication(Long applicationId, Boolean approved);

    /**
     * 获取用户的所有领养申请记录
     * @param userId 用户ID
     * @return 领养申请列表
     */
    List<AdoptionApplication> getUserAdoptionApplicationList(Long userId);

    /**
     * 宠物回访
     * @param petDTO 宠物访问信息
     */
    void petVisit(PetVisitDTO petDTO);

    /**
     * 获取宠物访问记录
     * @param queryDTO 查询参数
     * @return 宠物访问记录
     */
    PageInfo getPetVisitRecords(PageQueryListDTO queryDTO);

    /**
     * 修改宠物回访信息
     * @param petVisitDTO 回访信息（包含 id、petId、visitInfo、visitTime）
     */
    void updatePetVisit(PetVisitDTO petVisitDTO);

    /**
     * 删除宠物回访信息（物理删除）
     * @param id 回访记录 ID
     */
    void deletePetVisit(Long id);

    /**
     * AI 分析宠物回访信息
     * @param visitId 回访记录 ID
     */
    void aiAnalyzePetVisit(Long visitId);
}