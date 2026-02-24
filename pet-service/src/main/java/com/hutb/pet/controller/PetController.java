package com.hutb.pet.controller;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.pet.model.DTO.PetDTO;
import com.hutb.pet.model.DTO.PageQueryListDTO;
import com.hutb.pet.model.pojo.ResultInfo;
import com.hutb.pet.service.PetService;
import com.hutb.pet.model.DTO.AdoptPetRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pet")
public class PetController {
    
    @Autowired
    private PetService petService;

    /**
     * 新增宠物
     */
    @PostMapping("addPet")
    public ResultInfo addPet(@RequestBody PetDTO petDTO) {
        try {
            petService.addPet(petDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 删除宠物
     */
    @PostMapping("removePet")
    public ResultInfo removePet(@RequestParam Long id) {
        try {
            petService.removePet(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 更新宠物
     */
    @PostMapping("editPet")
    public ResultInfo updatePet(@RequestBody PetDTO petDTO) {
        try {
            petService.updatePet(petDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 查询宠物列表
     */
    @GetMapping("allUser/queryPetList")
    public ResultInfo queryPetList(@RequestBody PageQueryListDTO petQueryDTO) {
        try {
            return ResultInfo.success(petService.queryPetList(petQueryDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 领养宠物（提交领养申请）
     */
    @PostMapping("allUser/adoptPet")
    public ResultInfo adoptPet(@RequestBody AdoptPetRequestDTO adoptPetRequestDTO) {
        try {
            petService.adoptPet(adoptPetRequestDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取单个领养申请信息(供管理员使用)
     */
    @GetMapping("getAdoptionApplication")
    public ResultInfo getAdoptionApplication(@RequestParam Long id) {
        try {
            return ResultInfo.success(petService.getAdoptionApplicationById(id));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 获取某个宠物的所有申请记录(供管理员使用)
     */
    @GetMapping("getAdoptionApplicationsByPetId")
    public ResultInfo getAdoptionApplicationsByPetId(@RequestParam Long petId) {
        try {
            return ResultInfo.success(petService.getAdoptionApplicationsByPetId(petId));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 获取所有领养申请（供管理员使用）
     */
    @PostMapping("getAllAdoptionApplications")
    public ResultInfo getAllAdoptionApplications(@RequestBody PageQueryListDTO queryDTO) {
        try {
            return ResultInfo.success(petService.getAllAdoptionApplications(queryDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 审批领养申请
     */
    @PostMapping("approveAdoptionApplication")
    public ResultInfo approveAdoptionApplication(
            @RequestParam Long applicationId, 
            @RequestParam Boolean approved) {
        try {
            petService.approveAdoptionApplication(applicationId, approved);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
}