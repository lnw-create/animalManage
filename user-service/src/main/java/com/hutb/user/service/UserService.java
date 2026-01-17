package com.hutb.user.service;

import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.DTO.UserDTO;
import com.hutb.user.model.VO.LoginResponse;
import com.hutb.user.model.pojo.PageInfo;

import java.util.zip.DataFormatException;

public interface UserService {
    /**
     * 新增用户
     * @param userDTO 用户信息
     */
    void addUser(UserDTO userDTO) throws DataFormatException;

    /**
     * 删除用户
     * @param id 用户id
     */
    void removeUser(Long id) throws DataFormatException;

    /**
     * 更新用户
     * @param userDTO 用户信息
     */
    void updateUser(UserDTO userDTO) throws DataFormatException;

    /**
     * 查询用户列表
     * @param pageQueryListDTO 分页查询参数
     * @return 用户列表
     */
    PageInfo queryUserList(PageQueryListDTO pageQueryListDTO);
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录响应
     */
    LoginResponse login(String username, String password);
}
