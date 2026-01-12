package com.hutb.volunteerActivity.service;

import com.hutb.volunteerActivity.model.DTO.VolunteerActivityDTO;
import com.hutb.volunteerActivity.model.DTO.PageQueryListDTO;
import com.hutb.volunteerActivity.model.pojo.PageInfo;

public interface VolunteerActivityService {
    /**
     * 新增志愿活动
     * @param volunteerActivityDTO 志愿活动信息
     */
    void addVolunteerActivity(VolunteerActivityDTO volunteerActivityDTO);

    /**
     * 删除志愿活动
     * @param id 志愿活动id
     */
    void removeVolunteerActivity(Long id);

    /**
     * 更新志愿活动
     * @param volunteerActivityDTO 志愿活动信息
     */
    void updateVolunteerActivity(VolunteerActivityDTO volunteerActivityDTO);

    /**
     * 查询志愿活动列表
     * @param pageQueryListDTO 分页查询参数
     * @return 志愿活动列表
     */
    PageInfo queryVolunteerActivityList(PageQueryListDTO pageQueryListDTO);
}