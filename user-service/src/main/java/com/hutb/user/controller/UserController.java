package com.hutb.user.controller;

import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.DTO.UserDTO;
import com.hutb.user.model.pojo.ResultInfo;
import com.hutb.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 新增用户
     */
    @PostMapping("addUser")
    public ResultInfo addUser(@RequestBody UserDTO userDTO){
        userService.addUser(userDTO);
        return ResultInfo.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("removeUser")
    public ResultInfo removeUser(@RequestParam Long id){
        userService.removeUser(id);
        return ResultInfo.success();
    }

    /**
     * 更新用户
     */
    @PostMapping("editUser")
    public ResultInfo updateUser(@RequestBody UserDTO userDTO){
        userService.updateUser(userDTO);
        return ResultInfo.success();
    }

    /**
     * 查询用户列表
     */
    @GetMapping("queryUserList")
    public ResultInfo queryUserList(@RequestBody PageQueryListDTO pageQueryListDTO){
        return ResultInfo.success(userService.queryUserList(pageQueryListDTO));
    }
}