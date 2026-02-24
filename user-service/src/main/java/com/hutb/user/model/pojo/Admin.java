package com.hutb.user.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 管理员
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Admin {
    private Long id;
    private String username;
    private String password;
    private String realName;
    // super_admin-超级管理员 normal_admin-普通管理员
    private String role;
    private String phone;
    private String idCard;
    private String gender;
    // 1-正常 0-禁用 -1-删除
    private String status = "1";
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
}
