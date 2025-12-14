package com.hutb.animalmanage.service.impl;

import com.hutb.animalmanage.constant.AdminConstant;
import com.hutb.animalmanage.exception.CommonException;
import com.hutb.animalmanage.model.DTO.AdminDTO;
import com.hutb.animalmanage.model.DTO.PageQueryListDTO;
import com.hutb.animalmanage.model.pojo.PageInfo;
import com.hutb.animalmanage.model.VO.QueryAdminListVO;
import com.hutb.animalmanage.service.EmployeeService;
import com.hutb.animalmanage.utils.AdminCommonValidate;
import com.hutb.animalmanage.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    /**
     * 添加员工
     * @param adminDTO 员工信息
     */
    @Override
    public void addEmployee(AdminDTO adminDTO) {
        //1.todo 权限校验
        CommonUtils.permissionValidate(1L);
        //2. 参数校验
        AdminCommonValidate.validate(adminDTO);

        //3.todo 新增

    }

    /**
     * 删除员工
     * @param id 员工id
     */
    @Override
    public void removeEmployee(Long id) {
        //1.todo 权限校验
        CommonUtils.permissionValidate(1L);

        //2. 参数校验
        if (id == null || id <= 0){
            throw new CommonException("删除管理员的id不能为空");
        }

        //判断是否存在

        //3.todo 删除
    }

    /**
     * 更新员工信息
     * @param adminDTO 员工信息
     */
    @Override
    public void updateEmployee(AdminDTO adminDTO) {
        //1.todo 权限校验
        CommonUtils.permissionValidate(1L);

        //2. 参数校验
        AdminCommonValidate.validate(adminDTO);
        //其他参数校验
        if (CommonUtils.stringIsBlank(adminDTO.getRole()) || CommonUtils.stringIsBlank(adminDTO.getStatus())){
            throw new CommonException("更新管理员的角色或状态不能为空");
        }

        //3.todo 更新
    }

    /**
     * 查询员工列表
     * @param queryAdminListDTO 查询条件
     */
    @Override
    public PageInfo queryEmployeeList(PageQueryListDTO queryAdminListDTO) {
        //1.todo 权限校验
        CommonUtils.permissionValidate(1L);

        //2. 参数校验

        //不能查询已删除的管理员
        if (AdminConstant.ADMIN_STATUS_DELETE.equals(queryAdminListDTO.getStatus())){
            throw new CommonException("不能查询已删除的管理员");
        }

        //3. 查询数量并校验

        //4. 构建分页对象
        PageInfo<QueryAdminListVO> pageInfo = new PageInfo<>();

        //4. 查询列表

        //5. 构建返回结果
        return pageInfo;
    }
}
