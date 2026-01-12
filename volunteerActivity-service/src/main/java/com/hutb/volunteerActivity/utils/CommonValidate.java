package com.hutb.volunteerActivity.utils;


import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.CommonUtils;
import com.hutb.volunteerActivity.model.DTO.VolunteerActivityDTO;

/**
 * 简单参数校验
 */
public class CommonValidate {
    /**
     * 验证志愿活动信息
     * @param volunteerActivityDTO 志愿活动信息
     */
    public static void validateVolunteerActivity(VolunteerActivityDTO volunteerActivityDTO) {
        if (CommonUtils.stringIsBlank(volunteerActivityDTO.getActivityName())) {
            throw new CommonException("活动名称不能为空");
        }
        if (CommonUtils.stringIsBlank(volunteerActivityDTO.getDescription())) {
            throw new CommonException("活动描述不能为空");
        }
        if (CommonUtils.stringIsBlank(volunteerActivityDTO.getStartTime()))  {
            throw new CommonException("开始时间不能为空");
        }
        if (CommonUtils.stringIsBlank(volunteerActivityDTO.getEndTime())) {
            throw new CommonException("结束时间不能为空");
        }
        if (CommonUtils.stringIsBlank(volunteerActivityDTO.getLocation())) {
            throw new CommonException("活动地点不能为空");
        }
        if (volunteerActivityDTO.getMaxParticipants() == null || volunteerActivityDTO.getMaxParticipants() <= 0) {
            throw new CommonException("最大参与者数量必须大于0");
        }
        if (volunteerActivityDTO.getVolunteerHours() == null || volunteerActivityDTO.getVolunteerHours() <= 0) {
            throw new CommonException("志愿时长必须大于0");
        }
    }
}