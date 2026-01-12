package com.hutb.volunteerActivity.mapper;

import com.hutb.volunteerActivity.model.DTO.VolunteerActivityDTO;
import com.hutb.volunteerActivity.model.pojo.VolunteerActivity;
import com.hutb.volunteerActivity.model.DTO.PageQueryListDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VolunteerActivityMapper {
    /**
     * 新增志愿活动
     * @param volunteerActivityDTO 志愿活动信息
     */
    @Insert("insert into activity(activity_name, description, start_time, end_time, location, max_participants, current_participants, volunteer_hours, status, create_time, update_time, create_user, modified_user) " +
            "values(#{activityName}, #{description}, #{startTime}, #{endTime}, #{location}, #{maxParticipants}, #{currentParticipants},#{volunteerHours}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{modifiedUser})")
    void addVolunteerActivity(VolunteerActivityDTO volunteerActivityDTO);

    /**
     * 删除志愿活动
     * @param id 志愿活动id
     * @param status 状态
     */
    @Update("update activity set status = #{status}, modified_user = #{modifiedUser}, update_time = now() where id = #{id}")
    long removeVolunteerActivity(Long id, String status, String modifiedUser);

    /**
     * 根据id查询志愿活动信息
     * @param id 志愿活动id
     * @Param status 状态
     * @return 志愿活动信息
     */
    @Select("select * from activity where id = #{id} and status != #{status} ")
    VolunteerActivity queryVolunteerActivityById(Long id, String status);

    /**
     * 更新志愿活动信息
     * @param volunteerActivityDTO 志愿活动信息
     */
    @Update("update activity set activity_name = #{activityName}, description = #{description}, start_time = #{startTime}, " +
            "end_time = #{endTime}, location = #{location}, max_participants = #{maxParticipants}, current_participants = #{currentParticipants}," +
            "volunteer_hours = #{volunteerHours}, status = #{status}, modified_user = #{modifiedUser}, update_time = #{updateTime} where id = #{id}")
    long updateVolunteerActivity(VolunteerActivityDTO volunteerActivityDTO);

    /**
     * 查询志愿活动列表
     * @param pageQueryListDTO 查询条件
     * @return 志愿活动列表
     */
    List<VolunteerActivity> queryVolunteerActivityList(PageQueryListDTO pageQueryListDTO);

    /**
     * 根据活动名称查询志愿活动信息
     * @param activityName 活动名称
     * @Param status 状态
     * @return 志愿活动信息
     */
    @Select("select * from activity where activity_name = #{activityName} and status != #{status}")
    VolunteerActivity queryVolunteerActivityByActivityName(String activityName, String status);
}