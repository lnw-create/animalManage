package com.hutb.user.controller;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.DTO.VolunteerDTO;
import com.hutb.user.model.pojo.ResultInfo;
import com.hutb.user.service.VolunteerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 志愿者管理控制器
 */
@Slf4j
@RestController
@RequestMapping("userService/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    /**
     * 新增志愿者
     */
    @PostMapping("allUser/addVolunteer")
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
    @PostMapping("normalVolunteer/queryVolunteerList")
    public ResultInfo queryVolunteerList(@RequestBody PageQueryListDTO pageQueryListDTO) {
        try {
            return ResultInfo.success(volunteerService.queryVolunteerList(pageQueryListDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误：" + e.getMessage());
        }
    }

    /**
     * 查询用户积分余额
     * @param userId 用户 ID
     * @return 积分余额
     */
    @GetMapping("normalVolunteer/point/getPointBalance")
    public ResultInfo<Integer> getPointBalance(@RequestParam Long userId) {
        try {
            Integer balance = volunteerService.getPointBalance(userId);
            return ResultInfo.success(balance);
        } catch (CommonException e) {
            log.error("查询积分余额失败：userId={}", userId, e);
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            log.error("系统错误：userId={}", userId, e);
            return ResultInfo.fail("系统错误：" + e.getMessage());
        }
    }

    /**
     * 扣减用户积分
     * @param userId 用户 ID
     * @param points 积分数量
     * @return 操作结果
     */
    @PostMapping("normalVolunteer/point/deductPoints")
    public ResultInfo<Void> deductPoints(
        @RequestParam Long userId,
        @RequestParam Integer points
    ) {
        try {
            volunteerService.deductPoints(userId, points);
            return ResultInfo.success();
        } catch (CommonException e) {
            log.error("扣减积分失败：userId={}, points={}", userId, points, e);
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            log.error("系统错误：userId={}, points={}", userId, points, e);
            return ResultInfo.fail("系统错误：" + e.getMessage());
        }
    }

    /**
     * 增加用户积分
     * @param userId 用户 ID
     * @param points 积分数量
     * @return 操作结果
     */
    @PostMapping("normalVolunteer/point/addPoints")
    public ResultInfo<Void> addPoints(
        @RequestParam Long userId,
        @RequestParam Integer points
    ) {
        try {
            volunteerService.addPoints(userId, points);
            return ResultInfo.success();
        } catch (CommonException e) {
            log.error("增加积分失败：userId={}, points={}", userId, points, e);
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            log.error("系统错误：userId={}, points={}", userId, points, e);
            return ResultInfo.fail("系统错误：" + e.getMessage());
        }
    }
}