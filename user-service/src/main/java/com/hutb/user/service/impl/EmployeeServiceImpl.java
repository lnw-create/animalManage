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
import com.hutb.user.model.vo.LoginResponse;
import com.hutb.user.service.EmployeeService;
import com.hutb.user.utils.CommonValidate;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public LoginResponse login(String username, String password) {
        log.info("员工登录: username={}", username);
        
        // 1. 参数校验
        if (username == null || username.trim().isEmpty()) {
            throw new CommonException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new CommonException("密码不能为空");
        }
        
        // 2. 查询员工信息
        Admin admin = employeeMapper.queryAdminByUsername(username);
        if (admin == null) {
            throw new CommonException("用户名或密码错误");
        }
        
        // 3. 验证密码
        if (!password.equals(admin.getPassword())) {
            throw new CommonException("用户名或密码错误");
        }
        
        // 4. 检查员工状态
        if (!UserCommonConstant.ADMIN_STATUS_ENABLE.equals(admin.getStatus())) {
            throw new CommonException("账号已被禁用");
        }
        
        // 5. 生成JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", admin.getId());
        claims.put("username", admin.getUsername());
        claims.put("role", admin.getRole());

        // 使用与网关一致的密钥和过期时间
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        long timeout = 24 * 60 * 60 * 1000; // 24小时

        String token = com.hutb.commonUtils.utils.JwtUtil.createJwt(String.valueOf(secretKey), timeout, claims);
        
        log.info("员工登录成功: id={}", admin.getId());
        
        // 返回登录响应对象
        return new LoginResponse(admin.getId(), admin.getUsername(), admin.getRole(), token);
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
