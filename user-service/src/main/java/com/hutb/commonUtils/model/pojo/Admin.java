package com.hutb.commonUtils.model.pojo;

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
    // 1-超级管理员 0-普通管理员
    private String role;
    private String phone;
    private String email;
    // 1-正常 0-禁用 -1-删除
    private String status = "1";
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String modifiedUser;
}
