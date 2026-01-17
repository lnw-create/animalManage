package com.hutb.user.mapper;

import com.hutb.user.model.DTO.AdminDTO;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.pojo.Admin;
import com.hutb.user.model.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface employeeMapper {
    /**
     * 新增员工
     * @param adminDTO 员工信息
     */
    @Insert("insert into admin(username,password,real_name,phone,status,role,create_time,update_time,create_user,update_user) " +
            "values(#{username},#{password},#{realName},#{phone},#{status},#{role},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void addEmployee(AdminDTO adminDTO);

    /**
     * 删除员工
     * @param id 用户id
     * @param status 状态
     */
    @Update("update admin set status = #{status},update_user = #{updateUser}, update_time = now() where id = #{id}")
    long removeUser(Long id,String updateUser, String status);

    /**
     * 根据id查询员工信息
     * @param id 员工id
     * @return 员工信息
     */
    @Select("select * from admin where id = #{id} and status = '1' ")
    Admin queryAdminById(Long id);

    /**
     * 删除员工
     * @param id 员工id
     * @param updateUser 员工编号
     * @param status 员工状态
     */
    @Update("update admin set status = #{status},update_user = #{updateUser}, update_time = now() where id = #{id}")
    long removeEmployee(Long id, String updateUser, String status);

    /**
     * 更新员工信息
     * @param adminDTO 员工信息
     */
    @Update("update admin set username = #{username}, password = #{password}, real_name = #{realName}, " +
            "phone = #{phone}, update_user = #{updateUser}, status = #{status}, update_time = #{updateTime} where id = #{id}")
    long updateAdmin(AdminDTO adminDTO);

    /**
     * 查询员工列表
     * @param queryAdminListDTO 筛选条件
     * @return 员工列表
     */
    List<User> queryAdminList(PageQueryListDTO queryAdminListDTO);

    /**
     * 根据用户名查询员工信息
     * @param username 用户名
     * @return 员工信息
     */
    @Select("select * from admin where username = #{username} and status = '1' ")
    Admin queryAdminByUsername(String username);

    /**
     * 根据手机号查询员工信息
     * @param phone 手机号
     * @return 员工信息
     */
    @Select("select * from admin where phone = #{phone} and status = '1' ")
    User queryAdminByPhone(String phone);
}
