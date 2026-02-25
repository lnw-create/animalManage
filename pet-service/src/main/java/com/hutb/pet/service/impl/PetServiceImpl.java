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
        petDTO.setStatus(PetConstant.PET_STATUS_NORMAL);
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
        log.info("用户 {} 领养宠物: {}", UserContext.getUsername(), id);
        
        // 1. 验证宠物是否存在且可领养
        Pet pet = petMapper.queryPetById(id, PetConstant.ADOPTION_STATUS_ADOPTED);
        if (pet == null) {
            throw new CommonException("宠物信息不存在");
        }
        
        // 2. 检查用户是否已对该宠物提交申请
        int existingCount = adoptionApplicationMapper.checkExistingApplication(id, UserContext.getUserId(), PetConstant.ADOPTION_APPLICATION_STATUS_APPROVED);
        if (existingCount > 0) {
            throw new CommonException("您已对这只宠物提交了领养申请，请勿重复申请");
        }
        
        // 3. 创建领养申请记录
        AdoptionApplication application = new AdoptionApplication();
        application.setPetId(id);
        application.setUserId(UserContext.getUserId());
        application.setApplicantName(UserContext.getUsername());
        application.setApplicantPhone(adoptPetRequestDTO.getApplicantPhone());
        application.setApplicantAddress(adoptPetRequestDTO.getApplicantAddress());
        application.setApplicationReason(adoptPetRequestDTO.getApplicationReason());
        application.setStatus(PetConstant.ADOPTION_APPLICATION_STATUS_PENDING); // 待审批
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        application.setCreateUser(UserContext.getUsername());
        application.setUpdateUser(UserContext.getUsername());
        
        int result = adoptionApplicationMapper.createAdoptionApplication(application);
        if (result == 0) {
            throw new CommonException("提交领养申请失败");
        }
        
        // 4. 更新宠物状态为"已申请"
        petMapper.adoptPet(id, UserContext.getUsername(), PetConstant.ADOPTION_STATUS_APPLIED);
        
        log.info("领养申请提交成功，宠物ID: {}", id);
    }

    /**
     * 查询领养申请详情
     * @param id 领养申请id
     * @return 领养申请详情
     */
    @Override
    public AdoptionApplication getAdoptionApplicationById(Long id) {
        log.info("查询领养申请详情: {}", id);
        AdoptionApplication application = adoptionApplicationMapper.getAdoptionApplicationById(id);
        log.info("查询领养申请详情成功");
        return application;
    }
    
    /**
     * 查询宠物的所有申请记录
     * @param petId 宠物id
     * @return 宠物的所有申请记录
     */
    @Override
    public List<AdoptionApplication> getAdoptionApplicationsByPetId(Long petId) {
        log.info("查询宠物的所有申请记录: {}", petId);
        List<AdoptionApplication> applications = adoptionApplicationMapper.getAdoptionApplicationsByPetId(petId);
        log.info("查询宠物申请记录成功，共{}条", applications.size());
        return applications;
    }

    /**
     * 查询所有领养申请
     * @param queryDTO 分页查询参数
     * @return 领养申请列表
     */
    @Override
    public PageInfo getAllAdoptionApplications(PageQueryListDTO queryDTO) {
        log.info("查询所有领养申请: {}", queryDTO);
        
        Page<Object> page = PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<AdoptionApplication> applications = adoptionApplicationMapper.getAllAdoptionApplications(queryDTO);
        com.github.pagehelper.PageInfo<AdoptionApplication> pageInfo = new com.github.pagehelper.PageInfo<>(applications);
        
        log.info("查询领养申请成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }
    
    /**
     * 审批领养申请
     * @param applicationId 申请id
     * @param approved 是否批准
     */
    @Override
    public void approveAdoptionApplication(Long applicationId, Boolean approved) {
        log.info("审批领养申请: {}, 批准: {}", applicationId, approved);

        if (applicationId == null || applicationId <= 0) {
            throw new CommonException("领养申请ID不能为空");
        }
        // 2. 更新领养申请状态
        int i = adoptionApplicationMapper.updateAdoptionApplicationStatus(applicationId,
                approved ? PetConstant.ADOPTION_APPLICATION_STATUS_APPROVED : PetConstant.ADOPTION_APPLICATION_STATUS_REJECTED,
                UserContext.getUsername());
        if (i == 0) {
            throw new CommonException("更新领养申请状态失败");
        }

        //3.若审批通过，批量拒绝同一宠物的其他申请
        if (approved) {
            //获取宠物id
            Long petId = adoptionApplicationMapper.getAdoptionApplicationById(applicationId).getPetId();
            int i1 = adoptionApplicationMapper.batchRejectOtherApplications(petId,applicationId,
                    PetConstant.ADOPTION_APPLICATION_STATUS_REJECTED,
                    UserContext.getUsername());
            if (i1 == 0) {
                throw new CommonException("批量拒绝其他申请失败");
            }
        }
        log.info("领养申请审批完成，申请ID: {}，结果: {}", applicationId, approved ? "批准" : "拒绝");
    }
}