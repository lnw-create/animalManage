package com.hutb.animalmanage.service;

import com.hutb.animalmanage.model.DTO.AdminDTO;
import com.hutb.animalmanage.model.DTO.PageQueryListDTO;
import com.hutb.animalmanage.model.pojo.PageInfo;

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
}
