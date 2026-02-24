package com.hutb.user.controller;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.user.model.DTO.AdminDTO;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.pojo.ResultInfo;
import com.hutb.user.model.DTO.LoginRequest;
import com.hutb.user.model.VO.LoginResponse;
import com.hutb.user.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("userService/admin")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     */
    @PostMapping("normalAdmin/login")
    public ResultInfo login(@RequestBody LoginRequest loginRequest){
        try {
            LoginResponse loginResponse = employeeService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResultInfo.success(loginResponse);
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 新增员工
     */
    @PostMapping("superAdmin/addEmployee")
    public ResultInfo addEmployee(@RequestBody AdminDTO adminDTO){
        try {
            employeeService.addEmployee(adminDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 删除员工
     */
    @PostMapping("superAdmin/removeEmployee")
    public ResultInfo removeEmployee(@RequestParam Long id){
        try {
            employeeService.removeEmployee(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 更新员工
     */
    @PostMapping("normalAdmin/editEmployee")
    public ResultInfo updateEmployee(@RequestBody AdminDTO adminDTO){
        try {
            employeeService.updateEmployee(adminDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 查询员工列表
     */
    @GetMapping("normalAdmin/queryEmployeeList")
    public ResultInfo queryEmployeeList(@RequestBody PageQueryListDTO pageQueryListDTO){
        try {
            return ResultInfo.success(employeeService.queryEmployeeList(pageQueryListDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
}