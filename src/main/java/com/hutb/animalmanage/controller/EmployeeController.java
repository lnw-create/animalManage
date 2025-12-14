package com.hutb.animalmanage.controller;

import com.hutb.animalmanage.model.DTO.AdminDTO;
import com.hutb.animalmanage.model.DTO.PageQueryListDTO;
import com.hutb.animalmanage.model.pojo.ResultInfo;
import com.hutb.animalmanage.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 新增员工
     */
    @PostMapping("addEmployee")
    public ResultInfo addEmployee(@RequestBody AdminDTO adminDTO){
        employeeService.addEmployee(adminDTO);
        return ResultInfo.success();
    }

    /**
     * 删除员工
     */
    @DeleteMapping("removeEmployee")
    public ResultInfo removeEmployee(@RequestParam Long id){
        employeeService.removeEmployee(id);
        return ResultInfo.success();
    }

    /**
     * 更新员工
     */
    @PostMapping("updateEmployee")
    public ResultInfo updateEmployee(@RequestBody AdminDTO adminDTO){
        employeeService.updateEmployee(adminDTO);
        return ResultInfo.success();
    }

    /**
     * 查询员工列表
     */
    @GetMapping("queryEmployeeList")
    public ResultInfo queryEmployeeList(@RequestBody PageQueryListDTO queryAdminListDTO){
        return ResultInfo.success(employeeService.queryEmployeeList(queryAdminListDTO));
    }
}