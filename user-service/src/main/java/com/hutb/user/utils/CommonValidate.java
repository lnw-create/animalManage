package com.hutb.user.utils;

import com.hutb.commonUtils.utils.CommonUtils;
import com.hutb.user.model.DTO.AdminDTO;
import com.hutb.user.model.DTO.UserDTO;

/**
 * 简单参数校验
 */
public class CommonValidate {
    /**
     * 管理员通用简单参数校验
     */
    public static void adminValidate(AdminDTO adminDTO) {
        String username = adminDTO.getUsername();
        String password = adminDTO.getPassword();
        String realName = adminDTO.getRealName();
        String phone = adminDTO.getPhone();
        String email = adminDTO.getEmail();

        if (CommonUtils.stringIsBlank(username)){
            throw new RuntimeException("用户名不能为空");
        }
        if (CommonUtils.stringIsBlank(password)){
            throw new RuntimeException("密码不能为空");
        }
        if (CommonUtils.stringIsBlank(realName)){
            throw new RuntimeException("真实姓名不能为空");
        }
        if (CommonUtils.stringIsBlank(phone)){
            throw new RuntimeException("手机号不能为空");
        }
        if (CommonUtils.stringIsBlank(email)){
            throw new RuntimeException("邮箱不能为空");
        }
    }

    /**
     * 用户通用简单参数校验
     */
    public static void userValidate(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String realName = userDTO.getRealName();
        String phone = userDTO.getPhone();
        String email = userDTO.getEmail();

        if (CommonUtils.stringIsBlank(username)){
            throw new RuntimeException("用户名不能为空");
        }
        if (CommonUtils.stringIsBlank(password)){
            throw new RuntimeException("密码不能为空");
        }
        if (CommonUtils.stringIsBlank(realName)){
            throw new RuntimeException("真实姓名不能为空");
        }
        if (CommonUtils.stringIsBlank(phone)){
            throw new RuntimeException("手机号不能为空");
        }
        if (CommonUtils.stringIsBlank(email)){
            throw new RuntimeException("邮箱不能为空");
        }
    }
}