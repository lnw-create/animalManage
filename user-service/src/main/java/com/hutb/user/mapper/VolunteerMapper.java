package com.hutb.user.mapper;

import com.hutb.user.model.DTO.VolunteerDTO;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.pojo.Volunteer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VolunteerMapper {
    /**
     * 新增志愿者
     * @param volunteerDTO 志愿者信息
     */
    @Insert("insert into volunteer(user_id,username,real_name,id_card,phone,address,total_hours,activity_count,status,create_time,update_time,create_user,update_user) " +
            "values(#{userId},#{username},#{realName},#{idCard},#{phone},#{address},#{totalHours},#{activityCount},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void addVolunteer(VolunteerDTO volunteerDTO);

    /**
     * 根据志愿者id删除志愿者
     * @param id 志愿者id
     * @param updateUser 修改人
     * @param status 状态
     */
    @Update("update volunteer set status = #{status},update_user = #{updateUser}, update_time = now() where id = #{id}")
    long removeVolunteer(Long id, String updateUser, String status);

    /**
     * 根据用户id删除志愿者
     * @param userId 志愿者id
     * @param updateUser 修改人
     * @param status 状态
     */
    @Update("update volunteer set status = #{status},update_user = #{updateUser}, update_time = now() where userId = #{userId}")
    long removeVolunteerByUserId(Long userId, String updateUser, String status);

    /**
     * 根据id查询志愿者信息
     * @param id 志愿者id
     * @return 志愿者信息
     */
    @Select("select * from volunteer where id = #{id} and status = '1' ")
    Volunteer queryVolunteerById(Long id);

    /**
     * 更新志愿者信息
     * @param volunteerDTO 志愿者信息
     */
    long updateVolunteer(VolunteerDTO volunteerDTO);

    /**
     * 查询志愿者列表
     * @param pageQueryListDTO 查询条件
     * @return 志愿者列表
     */
    List<Volunteer> queryVolunteerList(PageQueryListDTO pageQueryListDTO);

    /**
     * 根据手机号查询志愿者信息
     * @param phone 手机号
     * @return 志愿者信息
     */
    @Select("select * from volunteer where phone = #{phone} and status = '1'")
    Volunteer queryVolunteerByPhone(String phone);

    /**
     * 根据用户名查询志愿者信息
     * @param username 用户名
     * @return 志愿者信息
     */
    @Select("select * from volunteer where username = #{username} and status = '1'")
    Volunteer queryVolunteerByUsername(String username);

    /**
     * 根据用户id查询志愿者信息
     * @param userId 用户id
     * @return 志愿者信息
     */
    @Select("select * from volunteer where user_id = #{userId} and status = '1'")
    Volunteer queryVolunteerByUserId(Long userId);
}