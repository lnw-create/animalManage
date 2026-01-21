package com.hutb.pet.controller;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.pet.model.DTO.PetDTO;
import com.hutb.pet.model.DTO.PageQueryListDTO;
import com.hutb.pet.model.pojo.ResultInfo;
import com.hutb.pet.service.PetService;
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
    @GetMapping("queryPetList")
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
     * 领养宠物
     */
    @PostMapping("adoptPet")
    public ResultInfo adoptPet(@RequestParam Long id) {
        try {
            petService.adoptPet(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
}