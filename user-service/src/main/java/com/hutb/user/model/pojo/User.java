package com.hutb.user.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String gender;
    //normal_user-普通用户 volunteer-志愿者
    private String role;
    private String phone;
    private String idCard;
    // 1-正常 0-禁用 -1-删除
    private String status = "1";
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
}
