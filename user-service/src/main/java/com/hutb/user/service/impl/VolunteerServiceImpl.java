package com.hutb.user.service.impl;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.UserContext;
import com.hutb.user.constant.UserCommonConstant;
import com.hutb.user.mapper.UserMapper;
import com.hutb.user.mapper.VolunteerMapper;
import com.hutb.user.model.DTO.VolunteerDTO;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class VolunteerServiceImpl implements VolunteerService {

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 添加志愿者
     * @param volunteerDTO 志愿者信息
     */
    @Transactional
    @Override
    public void addVolunteer(VolunteerDTO volunteerDTO) throws CommonException {
        log.info("添加志愿者:{}", volunteerDTO);
        // 1. 参数校验
        CommonValidate.volunteerValidate(volunteerDTO);

        if (volunteerDTO.getUserId() == null){
            throw new RuntimeException("用户ID不能为空");
        }


        // 2. 判断志愿者是否存在
        Volunteer volunteer = volunteerMapper.queryVolunteerByUserId(volunteerDTO.getUserId());
        if (volunteer != null) {
            throw new CommonException("志愿者已存在");
        }

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
        volunteerDTO.setGender(user.getGender());
        volunteerDTO.setIdCard(user.getIdCard());

        // 3. 新增
        volunteerDTO.setCreateTime(new Date());
        volunteerDTO.setUpdateTime(new Date());
        volunteerDTO.setCreateUser(UserContext.getUsername());
        volunteerDTO.setUpdateUser(UserContext.getUsername());
        volunteerDTO.setStatus(UserCommonConstant.USER_STATUS_ENABLE);
        volunteerMapper.addVolunteer(volunteerDTO);

        //4.修改用户表role
        int i = userMapper.updateUserById(volunteerDTO.getUserId(), UserCommonConstant.USER_ROLE_VOLUNTEER, UserContext.getUsername());
        if (i == 0) {
            throw new CommonException("修改用户角色失败");
        }
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

        // 3.更新志愿者
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
}