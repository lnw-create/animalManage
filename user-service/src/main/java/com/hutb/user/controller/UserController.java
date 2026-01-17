package com.hutb.user.controller;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.DTO.UserDTO;
import com.hutb.user.model.VO.LoginResponse;
import com.hutb.user.model.pojo.ResultInfo;
import com.hutb.user.model.DTO.LoginRequest;
import com.hutb.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("login")
    public ResultInfo login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResultInfo.success(loginResponse);
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 新增用户
     */
    @PostMapping("addUser")
    public ResultInfo addUser(@RequestBody UserDTO userDTO) {
        try {
            userService.addUser(userDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @PostMapping("removeUser")
    public ResultInfo removeUser(@RequestParam Long id){
        try {
            userService.removeUser(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @PostMapping("editUser")
    public ResultInfo updateUser(@RequestBody UserDTO userDTO) {
        try {
            userService.updateUser(userDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 查询用户列表
     */
    @GetMapping("queryUserList")
    public ResultInfo queryUserList(@RequestBody PageQueryListDTO pageQueryListDTO){
        try {
            return ResultInfo.success(userService.queryUserList(pageQueryListDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
}