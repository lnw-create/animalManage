package com.hutb.volunteerActivity.client;

import com.hutb.volunteerActivity.model.pojo.ResultInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 志愿者服务 Feign 客户端
 * 用于调用 user-service 的积分管理接口
 */
@FeignClient(name = "user-service", path = "/userService/volunteer")
public interface VolunteerServiceClient {

    /**
     * 查询用户积分余额
     * @param userId 用户 ID
     * @return 积分余额
     */
    @GetMapping("normalVolunteer/point/getPointBalance")
    ResultInfo<Integer> getPointBalance(@RequestParam Long userId);

    /**
     * 扣减用户积分
     * @param userId 用户 ID
     * @param points 积分数量
     * @return 操作结果
     */
    @PostMapping("normalVolunteer/point/deductPoints")
    ResultInfo<Void> deductPoints(
            @RequestParam Long userId,
            @RequestParam Integer points
    );

    /**
     * 增加用户积分
     * @param userId 用户 ID
     * @param points 积分数量
     * @return 操作结果
     */
    @PostMapping("normalVolunteer/point/addPoints")
    ResultInfo<Void> addPoints(
            @RequestParam Long userId,
            @RequestParam Integer points
    );
}
