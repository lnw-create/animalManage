package com.hutb.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hutb.commonUtils.exception.CommonException;
import com.hutb.user.constant.UserCommonConstant;
import com.hutb.user.mapper.userMapper;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.DTO.UserDTO;
import com.hutb.user.model.pojo.PageInfo;
import com.hutb.user.model.pojo.User;
import com.hutb.user.service.UserService;
import com.hutb.user.utils.CommonValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private userMapper userMapper;

    /**
     * 添加用户
     * @param userDTO 用户信息
     */
    @Override
    public void addUser(UserDTO userDTO) throws CommonException {
        log.info("添加用户:{}",userDTO);
        //1. 参数校验
        CommonValidate.userValidate(userDTO);

        //2. 判断用户是否存在
        queryUserByUsernameAndPhone(userDTO);

        //3.todo 新增
        userDTO.setStatus(UserCommonConstant.USER_STATUS_ENABLE);
        userDTO.setCreateUser("1");
        userDTO.setModifiedUser("1");
        userDTO.setCreateTime(new Date());
        userDTO.setUpdateTime(new Date());
        userMapper.addUser(userDTO);
        log.info("添加用户成功");
    }

    /**
     * 删除用户
     * @param id 用户id
     */
    @Override
    public void removeUser(Long id) throws CommonException {
        log.info("删除用户:id-{}",id);
        //1.参数校验
        if (id == null || id <= 0){
            throw new CommonException("删除用户id不能为空");
        }
        //2.判断用户是否存在
        User user = userMapper.queryUserById(id);
        if (user == null){
            throw new CommonException("用户不存在");
        }
        //3.删除 todo设置修改人
        long remove = userMapper.removeUser(id, "1", UserCommonConstant.USER_STATUS_DELETE);
        if (remove == 0){
            throw new CommonException("删除用户失败");
        }
        log.info("删除用户成功");
    }

    /**
     * 更新用户
     * @param userDTO 用户信息
     */
    @Override
    public void updateUser(UserDTO userDTO) throws CommonException {
        log.info("更新用户信息:{}",userDTO);
        //1.参数校验
        Long id = userDTO.getId();
        if (id == null || id <= 0){
            throw new CommonException("更新用户id不能为空");
        }
        CommonValidate.userValidate(userDTO);

        //2.查询用户信息
            User user = userMapper.queryUserById(id);
        if (user == null){
            throw new CommonException("用户信息不存在");
        }

        //3.查询更新的用户信息是否存在
        queryUserByUsernameAndPhone(userDTO);

        //4.todo 更新用户
        userDTO.setModifiedUser("1");
        userDTO.setUpdateTime(new Date());
        long update = userMapper.updateUser(userDTO);
        if (update == 0){
            throw new CommonException("更新用户信息失败");
        }
        log.info("更新用户信息成功");
    }

    /**
     * 查询用户列表
     * @param pageQueryListDTO 分页查询参数
     * @return 用户列表
     */
    @Override
    public PageInfo queryUserList(PageQueryListDTO pageQueryListDTO) {
        log.info("查询用户列表:{}",pageQueryListDTO);
        //分页查询
        Page<Object> page = PageHelper.startPage(pageQueryListDTO.getPageNum(), pageQueryListDTO.getPageSize());
        List<User> users = userMapper.queryUserList(pageQueryListDTO);
        com.github.pagehelper.PageInfo<User> pageInfo = new com.github.pagehelper.PageInfo<>(users);
        log.info("查询用户列表成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 根据手机号和姓名查询用户信息
     * @param user 用户信息
     */
    private void queryUserByUsernameAndPhone(UserDTO user){
        User userAnother = userMapper.queryUserByUsername(user.getUsername());
        User userAnotherAnother = userMapper.queryUserByPhone(user.getPhone());
        if (userAnother != null && !userAnother.getId().equals(user.getId())){
            throw new CommonException("用户名已存在");
        }
        if (userAnotherAnother != null && !userAnotherAnother.getId().equals(user.getId())){
            throw new CommonException("手机号已存在");
        }
    }
}