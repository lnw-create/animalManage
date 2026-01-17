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
    @Insert("insert into user(username,password,real_name,phone,status,create_time,update_time,create_user,update_user) " +
            "values(#{username},#{password},#{realName},#{phone},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void addUser(UserDTO userDTO);

    /**
     * 删除用户
     * @param id 用户id
     * @param status 状态
     */
    @Update("update user set status = #{status},update_user = #{updateUser}, update_time = now() where id = #{id}")
    long removeUser(Long id,String updateUser, String status);

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
            "phone = #{phone}, update_user = #{updateUser}, update_time = #{updateTime} where id = #{id}")
    long updateUser(UserDTO userDTO);

    /**
     * 查询用户列表
     * @param pageQueryListDTO 查询条件
     * @return 用户列表
     */
    List<User> queryUserList(PageQueryListDTO pageQueryListDTO);

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @Select("select * from user where username = #{username} and status = '1'")
    User queryUserByUsername(String username);


    /**
     * 根据手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("select * from user where phone = #{phone} and status = '1'")
    User queryUserByPhone(String phone);
}