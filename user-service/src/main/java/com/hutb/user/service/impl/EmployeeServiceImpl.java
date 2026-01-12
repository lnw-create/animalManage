package com.hutb.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hutb.commonUtils.exception.CommonException;
import com.hutb.user.constant.UserCommonConstant;
import com.hutb.user.mapper.employeeMapper;
import com.hutb.user.model.DTO.AdminDTO;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.pojo.Admin;
import com.hutb.user.model.pojo.PageInfo;
import com.hutb.user.model.pojo.User;
import com.hutb.user.service.EmployeeService;
import com.hutb.user.utils.CommonValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private employeeMapper employeeMapper;
    /**
     * 添加员工
     * @param adminDTO 员工信息
     */
    @Override
    public void addEmployee(AdminDTO adminDTO) {
        log.info("添加员工:{}",adminDTO);
        //1. 参数校验
        CommonValidate.adminValidate(adminDTO);

        //2. 判断员工是否存在
        queryEmployeeByUsernameAndPhone(adminDTO);

        //3.todo 新增
        adminDTO.setRole(UserCommonConstant.ADMIN_ROLE_NORMAL);
        adminDTO.setCreateUser("1");
        adminDTO.setModifiedUser("1");
        adminDTO.setCreateTime(new Date());
        adminDTO.setUpdateTime(new Date());
        employeeMapper.addEmployee(adminDTO);
        log.info("添加员工成功");
    }

    /**
     * 删除员工
     * @param id 员工id
     */
    @Override
    public void removeEmployee(Long id) {
        log.info("删除员工:id-{}",id);
        //1.参数校验
        if (id == null || id <= 0){
            throw new CommonException("删除员工id不能为空");
        }
        //2.判断员工是否存在
        Admin admin = employeeMapper.queryAdminById(id);
        if (admin == null){
            throw new CommonException("管理员不存在");
        }
        //3.删除 todo设置修改人
        long remove = employeeMapper.removeEmployee(id, "1", UserCommonConstant.ADMIN_STATUS_DELETE);
        if (remove == 0){
            throw new CommonException("删除员工失败");
        }
        log.info("删除员工成功");
    }

    /**
     * 更新员工信息
     * @param adminDTO 员工信息
     */
    @Override
    public void updateEmployee(AdminDTO adminDTO) {
        log.info("更新员工信息:{}",adminDTO);
        //1.参数校验
        Long id = adminDTO.getId();
        if (id == null || id <= 0){
            throw new CommonException("更新员工id不能为空");
        }
        CommonValidate.adminValidate(adminDTO);

        //2.查询用户信息
        Admin admin = employeeMapper.queryAdminById(id);
        if (admin == null){
            throw new CommonException("员工信息不存在");
        }

        //3.查询更新的用户信息是否存在
        queryEmployeeByUsernameAndPhone(adminDTO);

        //4.todo 更新用户
        adminDTO.setModifiedUser("1");
        adminDTO.setUpdateTime(new Date());
        long update = employeeMapper.updateAdmin(adminDTO);
        if (update == 0){
            throw new CommonException("更新员工信息失败");
        }
        log.info("更新员工信息成功");
    }

    /**
     * 查询员工列表
     * @param queryAdminListDTO 查询条件
     */
    @Override
    public PageInfo queryEmployeeList(PageQueryListDTO queryAdminListDTO) {
        log.info("查询员工列表:{}",queryAdminListDTO);
        //分页查询
        Page<Object> page = PageHelper.startPage(queryAdminListDTO.getPageNum(), queryAdminListDTO.getPageSize());
        List<User> users = employeeMapper.queryAdminList(queryAdminListDTO);
        com.github.pagehelper.PageInfo<User> pageInfo = new com.github.pagehelper.PageInfo<>(users);
        log.info("查询员工列表成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 根据手机号和姓名查询员工信息
     * @param admin 员工信息
     */
    private void queryEmployeeByUsernameAndPhone(AdminDTO admin){
        Admin adminA = employeeMapper.queryAdminByUsername(admin.getUsername());
        User adminB = employeeMapper.queryAdminByPhone(admin.getPhone());
        if (adminA != null && !adminA.getId().equals(admin.getId())){
            throw new CommonException("用户名已存在");
        }
        if (adminB != null && !adminB.getId().equals(admin.getId())){
            throw new CommonException("手机号已存在");
        }
    }
}
