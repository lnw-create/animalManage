package com.hutb.user.service;

import com.hutb.user.model.DTO.AdminDTO;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.pojo.PageInfo;
import com.hutb.user.model.vo.LoginResponse;

public interface EmployeeService {
    /**
     * 新增员工
     * @param adminDTO 员工信息
     */
    void addEmployee(AdminDTO adminDTO);

    /**
     * 删除员工
     * @param id 员工id
     */
    void removeEmployee(Long id);

    /**
     * 更新员工信息
     * @param adminDTO 员工信息
     */
    void updateEmployee(AdminDTO adminDTO);

    /**
     * 查询员工列表
     * @param queryAdminListDTO 查询条件
     * @return 员工列表
     */
    PageInfo queryEmployeeList(PageQueryListDTO queryAdminListDTO);
    
    /**
     * 员工登录
     * @param username 用户名
     * @param password 密码
     * @return 登录响应
     */
    LoginResponse login(String username, String password);
}