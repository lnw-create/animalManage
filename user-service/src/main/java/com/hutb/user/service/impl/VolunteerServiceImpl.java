package com.hutb.user.service.impl;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.UserContext;
import com.hutb.user.constant.UserCommonConstant;
import com.hutb.user.mapper.userMapper;
import com.hutb.user.mapper.volunteerMapper;
import com.hutb.user.model.DTO.VolunteerDTO;
import com.hutb.user.model.pojo.Admin;
import com.hutb.user.model.pojo.PageInfo;
import com.hutb.user.model.pojo.User;
import com.hutb.user.model.pojo.Volunteer;
import com.hutb.user.service.VolunteerService;
import com.hutb.user.utils.CommonValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import com.github.pagehelper.PageHelper;
import com.hutb.user.model.DTO.PageQueryListDTO;

@Service
@Slf4j
public class VolunteerServiceImpl implements VolunteerService {

    @Autowired
    private volunteerMapper volunteerMapper;

    @Autowired
    private userMapper userMapper;

    /**
     * 添加志愿者
     * @param volunteerDTO 志愿者信息
     */
    @Override
    public void addVolunteer(VolunteerDTO volunteerDTO) throws CommonException {
        log.info("添加志愿者:{}", volunteerDTO);
        // 1. 参数校验
        CommonValidate.volunteerValidate(volunteerDTO);

        // 2. 判断志愿者是否存在（根据手机号等唯一标识）
        queryVolunteerByUsernameAndPhone(volunteerDTO);

        // 3. 判断关联用户是否存在
        User user = userMapper.queryUserById(volunteerDTO.getUserId());
        if (user == null){
            throw new CommonException("关联用户不存在");
        }

        // 2. 设置志愿者信息
        volunteerDTO.setPhone(user.getPhone());
        volunteerDTO.setUsername(user.getUsername());
        volunteerDTO.setRealName(user.getRealName());
        volunteerDTO.setRole(user.getRole());

        // 3. 新增
        volunteerDTO.setCreateTime(new Date());
        volunteerDTO.setUpdateTime(new Date());
        volunteerDTO.setCreateUser(UserContext.getUsername());
        volunteerDTO.setUpdateUser(UserContext.getUsername());
        volunteerDTO.setStatus(UserCommonConstant.USER_STATUS_ENABLE);
        volunteerMapper.addVolunteer(volunteerDTO);
        log.info("添加志愿者成功");
    }

    /**
     * 删除志愿者
     * @param id 志愿者id
     */
    @Override
    public void removeVolunteer(Long id) throws CommonException {
        log.info("删除志愿者:id-{}", id);
        // 1.参数校验
        if (id == null || id <= 0) {
            throw new CommonException("删除志愿者id不能为空");
        }
        // 2.判断志愿者是否存在
        Volunteer volunteer = volunteerMapper.queryVolunteerById(id);
        if (volunteer == null) {
            throw new CommonException("志愿者不存在");
        }
        // 3.删除
        long remove = volunteerMapper.removeVolunteer(id, UserContext.getUsername(), UserCommonConstant.VOLUNTEER_STATUS_DELETE);
        if (remove == 0) {
            throw new CommonException("删除志愿者失败");
        }
        log.info("删除志愿者成功");
    }

    /**
     * 更新志愿者
     * @param volunteerDTO 志愿者信息
     */
    @Override
    public void updateVolunteer(VolunteerDTO volunteerDTO) throws CommonException {
        log.info("更新志愿者信息:{}", volunteerDTO);
        // 1.参数校验
        Long id = volunteerDTO.getId();
        if (id == null || id <= 0) {
            throw new CommonException("更新志愿者id不能为空");
        }
        CommonValidate.volunteerValidate(volunteerDTO);

        // 2.查询志愿者信息
        Volunteer volunteer = volunteerMapper.queryVolunteerById(id);
        if (volunteer == null) {
            throw new CommonException("志愿者信息不存在");
        }

        // 3.查询更新的志愿者信息是否存在
        queryVolunteerByUsernameAndPhone(volunteerDTO);

        // 4.更新志愿者
        volunteerDTO.setUpdateUser(UserContext.getUsername());
        volunteerDTO.setUpdateTime(new Date());
        long update = volunteerMapper.updateVolunteer(volunteerDTO);
        if (update == 0) {
            throw new CommonException("更新志愿者信息失败");
        }
        log.info("更新志愿者信息成功");
    }

    /**
     * 查询志愿者列表
     * @param pageQueryListDTO 分页查询参数
     * @return 志愿者列表
     */
    @Override
    public PageInfo queryVolunteerList(PageQueryListDTO pageQueryListDTO) {
        log.info("查询志愿者列表:{}", pageQueryListDTO);
        // 分页查询
        com.github.pagehelper.Page<Object> page = PageHelper.startPage(pageQueryListDTO.getPageNum(), pageQueryListDTO.getPageSize());
        List<Volunteer> volunteers = volunteerMapper.queryVolunteerList(pageQueryListDTO);
        com.github.pagehelper.PageInfo<Volunteer> pageInfo = new com.github.pagehelper.PageInfo<>(volunteers);
        log.info("查询志愿者列表成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 根据手机号和用户名查询志愿者信息，用于校验唯一性
     * @param volunteer 志愿者信息
     */
    private void queryVolunteerByUsernameAndPhone(VolunteerDTO volunteer) {
        Volunteer volunteerAnother = volunteerMapper.queryVolunteerByPhone(volunteer.getPhone());
        if (volunteerAnother != null && !volunteerAnother.getId().equals(volunteer.getId())) {
            throw new CommonException("手机号已存在");
        }
        
        // 检查用户名是否已存在
        Volunteer volunteerByUsername = volunteerMapper.queryVolunteerByUsername(volunteer.getUsername());
        if (volunteerByUsername != null && !volunteerByUsername.getId().equals(volunteer.getId())) {
            throw new CommonException("用户名已存在");
        }
    }
}