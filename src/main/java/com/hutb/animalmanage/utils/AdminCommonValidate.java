package com.hutb.animalmanage.utils;

import com.hutb.animalmanage.model.DTO.AdminDTO;

/**
 * 管理员通用简单参数校验
 */
public class AdminCommonValidate {
    /**
     * 校验参数是否为空
     */
    public static void validate(AdminDTO adminDTO) {
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
}