package com.hutb.animalmanage.service.impl;

import com.hutb.animalmanage.mapper.userMapper;
import com.hutb.animalmanage.model.DTO.PageQueryListDTO;
import com.hutb.animalmanage.model.DTO.UserDTO;
import com.hutb.animalmanage.model.pojo.PageInfo;
import com.hutb.animalmanage.service.UserService;
import com.hutb.animalmanage.utils.AdminCommonValidate;
import com.hutb.animalmanage.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private userMapper userMapper;

    /**
     * 添加用户
     * @param userDTO 用户信息
     */
    @Override
    public void addUser(UserDTO userDTO) {
        //1.todo 权限校验
        CommonUtils.permissionValidate(1L);
        //2. 参数校验

        //3.todo 新增
        userDTO.setCreateUser("1");
        userDTO.setModifiedUser("1");
        userDTO.setCreateTime(new Date());
        userDTO.setUpdateTime(new Date());
        userMapper.addUser(userDTO);
    }

    /**
     * 删除用户
     * @param id 用户id
     */
    @Override
    public void removeUser(Long id) {

    }

    /**
     * 更新用户
     * @param userDTO 用户信息
     */
    @Override
    public void updateUser(UserDTO userDTO) {

    }

    /**
     * 查询用户列表
     * @param pageQueryListDTO 分页查询参数
     * @return 用户列表
     */
    @Override
    public PageInfo queryUserList(PageQueryListDTO pageQueryListDTO) {
        return null;
    }
}
