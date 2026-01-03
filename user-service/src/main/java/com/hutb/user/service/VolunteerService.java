package com.hutb.user.service;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.DTO.VolunteerDTO;
import com.hutb.user.model.pojo.PageInfo;

public interface VolunteerService {
    /**
     * 新增志愿者
     * @param volunteerDTO 志愿者信息
     */
    void addVolunteer(VolunteerDTO volunteerDTO) throws CommonException;

    /**
     * 删除志愿者
     * @param id 志愿者id
     */
    void removeVolunteer(Long id) throws CommonException;

    /**
     * 更新志愿者
     * @param volunteerDTO 志愿者信息
     */
    void updateVolunteer(VolunteerDTO volunteerDTO) throws CommonException;

    /**
     * 查询志愿者列表
     * @param pageQueryListDTO 分页查询参数
     * @return 志愿者列表
     */
    PageInfo queryVolunteerList(PageQueryListDTO pageQueryListDTO);
}