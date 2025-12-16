package com.hutb.commonUtils.mapper;

import com.hutb.commonUtils.model.DTO.UserDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface userMapper {
    /**
     * 新增用户
     * @param userDTO 用户信息
     */
    @Insert("insert into user(username,password,real_name,phone,email,status,create_time,update_time,create_user,modified_user) " +
            "values(#{username},#{password},#{realName},#{phone},#{email},#{status},#{createTime},#{updateTime},#{createUser},#{modifiedUser})")
    void addUser(UserDTO userDTO);
}
