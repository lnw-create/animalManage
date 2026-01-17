package com.hutb.user.model.DTO;

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
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    // 1-正常 0-禁用 -1-删除
    private String status = "1";
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
}
