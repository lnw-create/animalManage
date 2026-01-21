package com.hutb.pet.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.UserContext;
import com.hutb.pet.constant.PetConstant;
import com.hutb.pet.mapper.PetMapper;
import com.hutb.pet.model.DTO.PetDTO;
import com.hutb.pet.model.pojo.PageInfo;
import com.hutb.pet.model.pojo.Pet;
import com.hutb.pet.service.PetService;
import com.hutb.pet.utils.CommonValidate;
import com.hutb.pet.model.DTO.PageQueryListDTO;
import com.hutb.pet.mapper.AdoptionApplicationMapper;
import com.hutb.pet.model.DTO.AdoptionApplicationDTO;
import com.hutb.pet.model.pojo.AdoptionApplication;
import com.hutb.pet.model.DTO.AdoptPetRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PetServiceImpl implements PetService {

    @Autowired
    private PetMapper petMapper;
    
    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;

    /**
     * 新增宠物
     * @param petDTO 宠物信息
     */
    @Override
    public void addPet(PetDTO petDTO) {
        log.info("添加宠物: {}", petDTO);

        // 1. 参数校验
        CommonValidate.validatePet(petDTO);

        // 2. 设置默认值
        petDTO.setAdoptionStatus(PetConstant.ADOPTION_STATUS_AVAILABLE); // 默认状态为待领养
        petDTO.setOwnerId(null); // 新增时ownerId默认为null
        petDTO.setCreateTime(LocalDateTime.now());
        petDTO.setUpdateTime(LocalDateTime.now());
        petDTO.setCreateUser(UserContext.getUsername());
        petDTO.setUpdateUser(UserContext.getUsername());

        // 3. 新增
        int i = petMapper.addPet(petDTO);
        if (i == 0) {
            throw new CommonException("添加宠物信息失败");
        }
        log.info("添加宠物成功");
    }

    /**
     * 删除宠物
     * @param id 宠物id
     */
    @Override
    public void removePet(Long id) {
        log.info("删除宠物信息:id-{}", id);

        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new CommonException("删除宠物id不能为空");
        }

        // 2. 判断宠物是否存在
        Pet pet = petMapper.queryPetById(id, PetConstant.PET_STATUS_DELETED);
        if (pet == null) {
            throw new CommonException("宠物信息不存在");
        }

        // 3. 删除
        long removed = petMapper.removePet(id, PetConstant.PET_STATUS_DELETED, UserContext.getUsername());
        if (removed == 0) {
            throw new CommonException("删除宠物信息失败");
        }
        log.info("删除宠物信息成功");
    }

    /**
     * 更新宠物
     * @param petDTO 宠物信息
     */
    @Override
    public void updatePet(PetDTO petDTO) {
        log.info("更新宠物信息: {}", petDTO);

        // 1. 参数校验
        Long id = petDTO.getId();
        if (id == null || id <= 0) {
            throw new CommonException("更新宠物id不能为空");
        }
        CommonValidate.validatePet(petDTO);

        Pet pet = petMapper.queryPetById(id, PetConstant.PET_STATUS_DELETED);
        if (pet == null) {
            throw new CommonException("宠物信息不存在");
        }

        // 3. 更新宠物
        petDTO.setUpdateTime(LocalDateTime.now());
        petDTO.setUpdateUser(UserContext.getUsername());
        long updated = petMapper.updatePet(petDTO);
        if (updated == 0) {
            throw new CommonException("更新宠物信息失败");
        }
        log.info("更新宠物信息成功");
    }

    /**
     * 查询宠物列表
      * @param petQueryDTO 分页查询参数
     * @return 宠物列表
     */
    @Override
    public PageInfo queryPetList(PageQueryListDTO petQueryDTO) {
        log.info("查询宠物列表: {}", petQueryDTO);

        // 分页查询
        Page<Object> page = PageHelper.startPage(petQueryDTO.getPageNum(), petQueryDTO.getPageSize());
        List<Pet> pets = petMapper.queryPetList(petQueryDTO);
        com.github.pagehelper.PageInfo<Pet> pageInfo = new com.github.pagehelper.PageInfo<>(pets);
        log.info("查询宠物列表成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 领养宠物（提交领养申请）
     * @param adoptPetRequestDTO 领养宠物请求参数
     */
    @Override
    public void adoptPet(AdoptPetRequestDTO adoptPetRequestDTO) {
        Long id = adoptPetRequestDTO.getPetId();
        String username = UserContext.getUsername();
        log.info("用户 {} 领养宠物: {}", username, id);
        
        // 1. 验证宠物是否存在且可领养
        Pet pet = petMapper.queryPetById(id, PetConstant.PET_STATUS_DELETED);
        if (pet == null) {
            throw new CommonException("宠物信息不存在");
        }
        
        if (!PetConstant.ADOPTION_STATUS_AVAILABLE.equals(pet.getAdoptionStatus())) {
            throw new CommonException("该宠物目前不可领养");
        }
        
        // 2. 检查用户是否已对该宠物提交申请
        int existingCount = adoptionApplicationMapper.checkExistingApplication(id, UserContext.getUserId());
        if (existingCount > 0) {
            throw new CommonException("您已对这只宠物提交了领养申请，请勿重复申请");
        }
        
        // 3. 创建领养申请记录
        AdoptionApplication application = new AdoptionApplication();
        application.setPetId(id);
        application.setUserId(UserContext.getUserId());
        application.setApplicantName(username);
        application.setApplicantPhone(adoptPetRequestDTO.getApplicantPhone());
        application.setApplicantAddress(adoptPetRequestDTO.getApplicantAddress());
        application.setApplicationReason(adoptPetRequestDTO.getApplicationReason());
        application.setStatus(PetConstant.ADOPTION_APPLICATION_STATUS_PENDING); // 待审批
        application.setCreateTime(java.time.LocalDateTime.now());
        application.setUpdateTime(java.time.LocalDateTime.now());
        application.setCreateUser(username);
        application.setUpdateUser(username);
        
        int result = adoptionApplicationMapper.createAdoptionApplication(application);
        if (result == 0) {
            throw new CommonException("提交领养申请失败");
        }
        
        // 4. 更新宠物状态为"已申请"
        petMapper.adoptPet(id, username, PetConstant.ADOPTION_STATUS_APPLIED);
        
        log.info("领养申请提交成功，宠物ID: {}", id);
    }
    
    @Override
    public AdoptionApplication getAdoptionApplicationById(Long id) {
        log.info("查询领养申请详情: {}", id);
        AdoptionApplication application = adoptionApplicationMapper.getAdoptionApplicationById(id);
        log.info("查询领养申请详情成功");
        return application;
    }
    
    @Override
    public List<AdoptionApplication> getAdoptionApplicationsByPetId(Long petId) {
        log.info("查询宠物的所有申请记录: {}", petId);
        List<AdoptionApplication> applications = adoptionApplicationMapper.getAdoptionApplicationsByPetId(petId);
        log.info("查询宠物申请记录成功，共{}条", applications.size());
        return applications;
    }
    
    @Override
    public PageInfo getAllAdoptionApplications(PageQueryListDTO queryDTO) {
        log.info("查询所有领养申请: {}", queryDTO);
        
        Page<Object> page = PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<AdoptionApplication> applications = adoptionApplicationMapper.getAllAdoptionApplications(queryDTO);
        com.github.pagehelper.PageInfo<AdoptionApplication> pageInfo = new com.github.pagehelper.PageInfo<>(applications);
        
        log.info("查询领养申请成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }
    
    @Override
    public void approveAdoptionApplication(Long applicationId, Boolean approved) {
        log.info("审批领养申请: {}, 批准: {}", applicationId, approved);
        
        // 1. 获取申请记录
        AdoptionApplication application = adoptionApplicationMapper.getAdoptionApplicationById(applicationId);
        if (application == null) {
            throw new CommonException("领养申请不存在");
        }
        
        // 2. 验证申请状态
        if (!PetConstant.ADOPTION_APPLICATION_STATUS_PENDING.equals(application.getStatus())) {
            throw new CommonException("该申请无法审批，状态不符合要求");
        }
        
        // 3. 获取宠物信息
        Pet pet = petMapper.queryPetById(application.getPetId(), PetConstant.PET_STATUS_DELETED);
        if (pet == null) {
            throw new CommonException("宠物信息不存在");
        }
        
        // 4. 更新申请状态
        String newStatus = approved ? PetConstant.ADOPTION_APPLICATION_STATUS_APPROVED : PetConstant.ADOPTION_APPLICATION_STATUS_REJECTED;
        int result = adoptionApplicationMapper.updateAdoptionApplicationStatus(applicationId, newStatus, UserContext.getUsername());
        if (result == 0) {
            throw new CommonException("更新申请状态失败");
        }
        
        // 5. 根据审批结果更新宠物状态
        if (approved) {
            // 审批通过：更新宠物状态为已领养，并设置领养人
            petMapper.updatePetOwner(application.getPetId(), application.getUserId(), 
                    PetConstant.ADOPTION_STATUS_ADOPTED, UserContext.getUsername());
        } else {
            // 审批拒绝：如果宠物仍为已申请状态，则恢复为待领养
            if (PetConstant.ADOPTION_STATUS_APPLIED.equals(pet.getAdoptionStatus())) {
                petMapper.adoptPet(application.getPetId(), UserContext.getUsername(), PetConstant.ADOPTION_STATUS_AVAILABLE);
            }
        }
        
        log.info("领养申请审批完成，申请ID: {}，结果: {}", applicationId, approved ? "批准" : "拒绝");
    }
}