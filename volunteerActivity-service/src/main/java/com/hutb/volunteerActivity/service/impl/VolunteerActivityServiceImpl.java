package com.hutb.volunteerActivity.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hutb.commonUtils.exception.CommonException;
import com.hutb.volunteerActivity.constant.VolunteerActivityCommonConstant;
import com.hutb.volunteerActivity.mapper.VolunteerActivityMapper;
import com.hutb.volunteerActivity.model.DTO.PageQueryListDTO;
import com.hutb.volunteerActivity.model.DTO.VolunteerActivityDTO;
import com.hutb.volunteerActivity.model.pojo.PageInfo;
import com.hutb.volunteerActivity.model.pojo.VolunteerActivity;
import com.hutb.volunteerActivity.service.VolunteerActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.hutb.volunteerActivity.utils.CommonValidate.validateVolunteerActivity;

@Service
@Slf4j
public class VolunteerActivityServiceImpl implements VolunteerActivityService {

    @Autowired
    private VolunteerActivityMapper volunteerActivityMapper;
    /**
     * 添加志愿活动
     * @param volunteerActivityDTO 志愿活动信息
     */
    @Override
    public void addVolunteerActivity(VolunteerActivityDTO volunteerActivityDTO) throws CommonException {
        log.info("添加志愿活动:{}", volunteerActivityDTO);
        // 1. 参数校验
        validateVolunteerActivity(volunteerActivityDTO);

        // 2. 判断志愿活动是否存在
        VolunteerActivity existingActivity = volunteerActivityMapper.queryVolunteerActivityByActivityName(volunteerActivityDTO.getActivityName(), VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
        if (existingActivity != null) {
            throw new CommonException("活动名称已存在");
        }

        // 3. 新增
        volunteerActivityDTO.setStatus(VolunteerActivityCommonConstant.ACTIVITY_STATUS_ENROLLING);
        volunteerActivityDTO.setCreateUser("1");
        volunteerActivityDTO.setModifiedUser("1");
        volunteerActivityDTO.setCreateTime(new Date());
        volunteerActivityDTO.setUpdateTime(new Date());
        volunteerActivityMapper.addVolunteerActivity(volunteerActivityDTO);
        log.info("添加志愿活动成功");
    }

    /**
     * 删除志愿活动
     * @param id 志愿活动id
     */
    @Override
    public void removeVolunteerActivity(Long id) throws CommonException {
        log.info("删除志愿活动:id-{}", id);
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new CommonException("删除志愿活动id不能为空");
        }
        // 2. 判断志愿活动是否存在
        VolunteerActivity activity = volunteerActivityMapper.queryVolunteerActivityById(id, VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
        if (activity == null) {
            throw new CommonException("志愿活动不存在");
        }
        // 3. 删除（设置为删除状态）
        long removed = volunteerActivityMapper.removeVolunteerActivity(id, VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED, "1");
        if (removed == 0) {
            throw new CommonException("删除志愿活动失败");
        }
        log.info("删除志愿活动成功");
    }

    /**
     * 更新志愿活动
     * @param volunteerActivityDTO 志愿活动信息
     */
    @Override
    public void updateVolunteerActivity(VolunteerActivityDTO volunteerActivityDTO) throws CommonException {
        log.info("更新志愿活动信息:{}", volunteerActivityDTO);
        // 1. 参数校验
        Long id = volunteerActivityDTO.getId();
        if (id == null || id <= 0) {
            throw new CommonException("更新志愿活动id不能为空");
        }
        validateVolunteerActivity(volunteerActivityDTO);

        // 2. 查询志愿活动信息
        VolunteerActivity activity = volunteerActivityMapper.queryVolunteerActivityById(id, VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
        if (activity == null) {
            throw new CommonException("志愿活动信息不存在");
        }

        // 3. 查询更新的活动名称是否已存在
        VolunteerActivity existingActivity = volunteerActivityMapper.queryVolunteerActivityByActivityName(volunteerActivityDTO.getActivityName(), VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
        if (existingActivity != null && !existingActivity.getId().equals(volunteerActivityDTO.getId())) {
            throw new CommonException("活动名称已存在");
        }

        // 4. 更新志愿活动
        volunteerActivityDTO.setModifiedUser("1");
        volunteerActivityDTO.setUpdateTime(new Date());
        long updated = volunteerActivityMapper.updateVolunteerActivity(volunteerActivityDTO);
        if (updated == 0) {
            throw new CommonException("更新志愿活动信息失败");
        }
        log.info("更新志愿活动信息成功");
    }

    /**
     * 查询志愿活动列表
     * @param pageQueryListDTO 分页查询参数
     * @return 志愿活动列表
     */
    @Override
    public PageInfo queryVolunteerActivityList(PageQueryListDTO pageQueryListDTO) {
        log.info("查询志愿活动列表:{}", pageQueryListDTO);
        // 分页查询
        Page<Object> page = PageHelper.startPage(pageQueryListDTO.getPageNum(), pageQueryListDTO.getPageSize());
        List<VolunteerActivity> activities = volunteerActivityMapper.queryVolunteerActivityList(pageQueryListDTO);
        com.github.pagehelper.PageInfo<VolunteerActivity> pageInfo = new com.github.pagehelper.PageInfo<>(activities);
        log.info("查询志愿活动列表成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }
}