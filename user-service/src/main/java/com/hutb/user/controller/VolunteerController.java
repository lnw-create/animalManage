package com.hutb.user.controller;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.DTO.VolunteerDTO;
import com.hutb.user.model.pojo.ResultInfo;
import com.hutb.user.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 志愿者管理控制器
 */
@RestController
@RequestMapping("/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    /**
     * 新增志愿者
     */
    @PostMapping("normalVolunteer/addVolunteer")
    public ResultInfo addVolunteer(@RequestBody VolunteerDTO volunteerDTO) {
        try {
            volunteerService.addVolunteer(volunteerDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 删除志愿者
     */
    @PostMapping("normalVolunteer/removeVolunteer")
    public ResultInfo removeVolunteer(@RequestParam Long id) {
        try {
            volunteerService.removeVolunteer(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 更新志愿者
     */
    @PostMapping("normalVolunteer/editVolunteer")
    public ResultInfo updateVolunteer(@RequestBody VolunteerDTO volunteerDTO) {
        try {
            volunteerService.updateVolunteer(volunteerDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 查询志愿者列表
     */
    @GetMapping("normalVolunteer/queryVolunteerList")
    public ResultInfo queryVolunteerList(@RequestBody PageQueryListDTO pageQueryListDTO) {
        try {
            return ResultInfo.success(volunteerService.queryVolunteerList(pageQueryListDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
}