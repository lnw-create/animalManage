package com.hutb.volunteerActivity.mapper;

import com.hutb.volunteerActivity.model.DTO.VolunteerActivityDTO;
import com.hutb.volunteerActivity.model.pojo.ActivityParticipant;
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
    @Insert("insert into activity(activity_name, description, start_time, end_time, location, max_participants, current_participants, volunteer_hours, status, create_time, update_time, create_user, update_user) " +
            "values(#{activityName}, #{description}, #{startTime}, #{endTime}, #{location}, #{maxParticipants}, #{currentParticipants},#{volunteerHours}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void addVolunteerActivity(VolunteerActivityDTO volunteerActivityDTO);

    /**
     * 删除志愿活动
     * @param id 志愿活动id
     * @param status 状态
     */
    @Update("update activity set status = #{status}, update_user = #{updateUser}, update_time = now() where id = #{id}")
    long removeVolunteerActivity(Long id, String status, String updateUser);

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
            "volunteer_hours = #{volunteerHours}, status = #{status}, update_user = #{updateUser}, update_time = #{updateTime} where id = #{id}")
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

    /**
     * 增加活动参与人数
     * @param id 活动 id
     * @param updateUser 更新人
     * @return 影响的行数
     */
    @Update("update activity set current_participants = current_participants + 1, update_user = #{updateUser}, update_time = now() where id = #{id} and status = '1' and current_participants < max_participants")
    int incrementParticipantCount(Long id, String updateUser);

    /**
     * 查询用户是否已参与活动
     * @param activityId 活动 id
     * @param userId 用户 id
     * @return 参与记录
     */
    @Select("select * from activity_participant where activity_id = #{activityId} and user_id = #{userId} and status = '1'")
    ActivityParticipant queryParticipantByActivityAndUser(Long activityId, Long userId);

    /**
     * 添加参与者记录
     * @param participant 参与者信息
     */
    @Insert("insert into activity_participant(activity_id, user_id, join_time, status, create_time, update_time, create_user, update_user) " +
            "values(#{activityId}, #{userId}, #{joinTime}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insertParticipant(ActivityParticipant participant);

    /**
     * 减少活动参与人数
     * @param id 活动 id
     * @param updateUser 更新人
     * @return 影响的行数
     */
    @Update("update activity set current_participants = current_participants - 1, update_user = #{updateUser}, update_time = now() where id = #{id} and current_participants > 0")
    int decrementParticipantCount(Long id, String updateUser);

    /**
     * 根据 ID 查询参与者记录
     * @param id 参与者记录 ID
     * @return 参与者信息
     */
    @Select("select * from activity_participant where id = #{id} and status != '-1'")
    ActivityParticipant queryParticipantById(Long id);

    /**
     * 根据活动和用户查询参与者记录（包含已取消的）
     * @param activityId 活动 ID
     * @param userId 用户 ID
     * @return 参与者信息
     */
    @Select("select * from activity_participant where activity_id = #{activityId} and user_id = #{userId}")
    ActivityParticipant queryParticipantByActivityAndUserAll(Long activityId, Long userId);

    /**
     * 更新参与者状态
     * @param id 参与者记录 ID
     * @param status 新状态
     * @param updateUser 更新人
     * @return 影响的行数
     */
    @Update("update activity_participant set status = #{status}, update_user = #{updateUser}, update_time = now() where id = #{id}")
    int updateParticipantStatus(Long id, String status, String updateUser);

    /**
     * 查询用户参与的活动列表
     * @param userId 用户 ID
     * @param status 活动状态（可选）
     * @return 志愿者活动列表
     */
    List<VolunteerActivity> queryMyActivities(Long userId, String status);

    /**
     * 查询活动中所有正常参与的参与者
     * @param activityId 活动 ID
     * @return 参与者列表
     */
    @Select("SELECT * FROM activity_participant WHERE activity_id = #{activityId} AND status = '1'")
    List<ActivityParticipant> queryNormalParticipantsByActivityId(Long activityId);

    /**
     * 增加用户参与活动次数
     * @param userId 用户 ID
     * @param updateUser 更新人
     * @return 影响的行数
     */
    @Update("UPDATE volunteer SET activity_count = activity_count + 1, update_user = #{updateUser}, update_time = now() WHERE user_id = #{userId}")
    int incrementActivityCount(Long userId, String updateUser);

    /**
     * 增加用户累计志愿时长
     * @param userId 用户 ID
     * @param hours 增加的时长
     * @param updateUser 更新人
     * @return 影响的行数
     */
    @Update("UPDATE volunteer SET total_hours = total_hours + #{hours}, update_user = #{updateUser}, update_time = now() WHERE user_id = #{userId}")
    int incrementTotalHours(Long userId, Double hours, String updateUser);
}