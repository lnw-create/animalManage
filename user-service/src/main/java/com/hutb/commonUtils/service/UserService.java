package com.hutb.commonUtils.service;

import com.hutb.commonUtils.model.DTO.PageQueryListDTO;
import com.hutb.commonUtils.model.DTO.UserDTO;
import com.hutb.commonUtils.model.pojo.PageInfo;

public interface UserService {
    /**
     * 新增用户
     * @param userDTO 用户信息
     */
    void addUser(UserDTO userDTO);

    /**
     * 删除用户
     * @param id 用户id
     */
    void removeUser(Long id);

    /**
     * 更新用户
     * @param userDTO 用户信息
     */
    void updateUser(UserDTO userDTO);

    /**
     * 查询用户列表
     * @param pageQueryListDTO 分页查询参数
     * @return 用户列表
     */
    PageInfo queryUserList(PageQueryListDTO pageQueryListDTO);
}
