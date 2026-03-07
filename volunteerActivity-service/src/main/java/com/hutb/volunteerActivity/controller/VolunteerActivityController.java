package com.hutb.volunteerActivity.controller;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.volunteerActivity.model.DTO.PageQueryListDTO;
import com.hutb.volunteerActivity.model.DTO.VolunteerActivityDTO;
import com.hutb.volunteerActivity.model.pojo.ResultInfo;
import com.hutb.volunteerActivity.service.VolunteerActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("volunteerActivity")
public class VolunteerActivityController {
    @Autowired
    private VolunteerActivityService volunteerActivityService;

    /**
     * 新增志愿活动
     */
    @PostMapping("addActivity")
    public ResultInfo addActivity(@RequestBody VolunteerActivityDTO volunteerActivityDTO) {
        try {
            volunteerActivityService.addVolunteerActivity(volunteerActivityDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 删除志愿活动
     */
    @PostMapping("removeActivity")
    public ResultInfo removeActivity(@RequestParam Long id) {
        try {
            volunteerActivityService.removeVolunteerActivity(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 更新志愿活动
     */
    @PostMapping("normalVolunteer/editActivity")
    public ResultInfo updateActivity(@RequestBody VolunteerActivityDTO volunteerActivityDTO) {
        try {
            volunteerActivityService.updateVolunteerActivity(volunteerActivityDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 查询志愿活动列表
     */
    @PostMapping("allUser/queryActivityList")
    public ResultInfo queryActivityList(@RequestBody PageQueryListDTO pageQueryListDTO) {
        try {
            return ResultInfo.success(volunteerActivityService.queryVolunteerActivityList(pageQueryListDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     *  参加志愿活动
     */
    @PostMapping("normalVolunteer/joinActivity")
    public ResultInfo joinActivity(@RequestParam Long id) {
        try {
            volunteerActivityService.joinActivity(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误：" + e.getMessage());
        }
    }

    /**
     * 取消志愿活动
     */
    @PostMapping("normalVolunteer/cancelActivity")
    public ResultInfo cancelActivity(@RequestParam Long id) {
        try {
            volunteerActivityService.cancelActivity(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误：" + e.getMessage());
        }
    }

    /**
     * 查询我的志愿活动
     */
    @PostMapping("normalVolunteer/myActivities")
    public ResultInfo queryMyActivities(@RequestBody PageQueryListDTO pageQueryListDTO) {
        try {
            return ResultInfo.success(volunteerActivityService.queryMyActivities(pageQueryListDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误：" + e.getMessage());
        }
    }
}