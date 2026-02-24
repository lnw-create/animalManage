package com.hutb.user.utils;

import com.hutb.commonUtils.utils.CommonUtils;
import com.hutb.user.model.DTO.AdminDTO;
import com.hutb.user.model.DTO.UserDTO;
import com.hutb.user.model.DTO.VolunteerDTO;

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
        String gender = adminDTO.getGender();
        String idCard = adminDTO.getIdCard();

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
        if (CommonUtils.stringIsBlank(gender)){
            throw new RuntimeException("性别不能为空");
        }
        if (CommonUtils.stringIsBlank(idCard)){
            throw new RuntimeException("身份证号不能为空");
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
        String gender = userDTO.getGender();
        String idCard = userDTO.getIdCard();

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
        if (CommonUtils.stringIsBlank(gender)){
            throw new RuntimeException("性别不能为空");
        }
        if (CommonUtils.stringIsBlank(idCard)){
            throw new RuntimeException("身份证号不能为空");
        }
    }

    /**
     * 志愿者通用简单参数校验
     */
    public static void volunteerValidate(VolunteerDTO volunteerDTO) {
        Long userId = volunteerDTO.getUserId();
        String idCard = volunteerDTO.getIdCard();
        String address = volunteerDTO.getAddress();

        if (userId == null){
            throw new RuntimeException("用户ID不能为空");
        }

        if (CommonUtils.stringIsBlank(idCard)){
            throw new RuntimeException("身份证号不能为空");
        }
        if (CommonUtils.stringIsBlank(address)){
            throw new RuntimeException("地址不能为空");
        }
    }
}