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
     * 领养宠物
     * @param id 宠物id
     */
    @Override
    public void adoptPet(Long id) {
        log.info("领养宠物: {}", id);
        Pet pet = petMapper.queryPetById(id, PetConstant.PET_STATUS_DELETED);
        if (pet == null) {
            throw new CommonException("宠物信息不存在");
        }
        //1. 更新宠物信息为已申请
        petMapper.adoptPet(id, UserContext.getUsername(),PetConstant.ADOPTION_STATUS_APPLIED);

        //2. todo 发送领养申请

        // todo 判断领养申请是否成功

        //3. 更新用户信息为已领养
        petMapper.adoptPet(id, UserContext.getUsername(), PetConstant.ADOPTION_STATUS_ADOPTED);
    }
}