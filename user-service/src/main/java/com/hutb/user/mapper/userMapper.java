package com.hutb.user.mapper;

import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.DTO.UserDTO;
import com.hutb.user.model.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface userMapper {
    /**
     * 新增用户
     * @param userDTO 用户信息
     */
    @Insert("insert into user(username,password,real_name,phone,email,status,create_time,update_time,create_user,modified_user) " +
            "values(#{username},#{password},#{realName},#{phone},#{email},#{status},#{createTime},#{updateTime},#{createUser},#{modifiedUser})")
    void addUser(UserDTO userDTO);

    /**
     * 删除用户
     * @param id 用户id
     * @param status 状态
     */
    @Update("update user set status = #{status} where id = #{id}")
    long removeUser(Long id, String status);

    /**
     * 根据id查询用户信息
     * @param id 用户id
     * @return 用户信息
     */
    @Select("select * from user where id = #{id} and status = '1' ")
    User queryUserById(Long id);

    /**
     * 更新用户信息
     * @param userDTO 用户信息
     */
    @Update("update user set username = #{username}, password = #{password}, real_name = #{realName}, " +
            "phone = #{phone}, email = #{email}, modified_user = #{modifiedUser}, update_time = {updateTime} where id = #{id}")
    long updateUser(UserDTO userDTO);

    /**
     * 查询用户列表
     * @param pageQueryListDTO 查询条件
     * @return 用户列表
     */
    List<User> queryUserList(PageQueryListDTO pageQueryListDTO);
}