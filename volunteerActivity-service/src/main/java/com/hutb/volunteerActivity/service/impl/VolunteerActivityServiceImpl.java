package com.hutb.volunteerActivity.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.UserContext;
import com.hutb.volunteerActivity.client.VolunteerServiceClient;
import com.hutb.volunteerActivity.constant.VolunteerActivityCommonConstant;
import com.hutb.volunteerActivity.mapper.VolunteerActivityMapper;
import com.hutb.volunteerActivity.model.DTO.PageQueryListDTO;
import com.hutb.volunteerActivity.model.DTO.VolunteerActivityDTO;
import com.hutb.volunteerActivity.model.pojo.ActivityParticipant;
import com.hutb.volunteerActivity.model.pojo.PageInfo;
import com.hutb.volunteerActivity.model.pojo.ResultInfo;
import com.hutb.volunteerActivity.model.pojo.VolunteerActivity;
import com.hutb.volunteerActivity.service.VolunteerActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

import static com.hutb.volunteerActivity.utils.CommonValidate.validateVolunteerActivity;

@Service
@Slf4j
public class VolunteerActivityServiceImpl implements VolunteerActivityService {

    @Autowired
    private VolunteerActivityMapper volunteerActivityMapper;

    @Autowired
    private VolunteerServiceClient volunteerServiceClient;
    /**
     * 添加志愿活动
     * @param volunteerActivityDTO 志愿活动信息
     */
    @Override
    public void addVolunteerActivity(VolunteerActivityDTO volunteerActivityDTO) throws CommonException {
        log.info("添加志愿活动:{}", volunteerActivityDTO);
        // 1. 参数校验
        validateVolunteerActivity(volunteerActivityDTO);

        // 2. 判断志愿活动是否存在
        VolunteerActivity existingActivity = volunteerActivityMapper.queryVolunteerActivityByActivityName(volunteerActivityDTO.getActivityName(), VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
        if (existingActivity != null) {
            throw new CommonException("活动名称已存在");
        }

        // 3. 新增
        volunteerActivityDTO.setStatus(VolunteerActivityCommonConstant.ACTIVITY_STATUS_ENROLLING);
        volunteerActivityDTO.setCreateUser(UserContext.getUsername());
        volunteerActivityDTO.setUpdateUser(UserContext.getUsername());
        volunteerActivityDTO.setCreateTime(new Date());
        volunteerActivityDTO.setUpdateTime(new Date());
        volunteerActivityMapper.addVolunteerActivity(volunteerActivityDTO);
        log.info("添加志愿活动成功");
    }

    /**
     * 删除志愿活动
     * @param id 志愿活动id
     */
    @Override
    public void removeVolunteerActivity(Long id) throws CommonException {
        log.info("删除志愿活动:id-{}", id);
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new CommonException("删除志愿活动id不能为空");
        }
        // 2. 判断志愿活动是否存在
        VolunteerActivity activity = volunteerActivityMapper.queryVolunteerActivityById(id, VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
        if (activity == null) {
            throw new CommonException("志愿活动不存在");
        }
        // 3. 删除（设置为删除状态）
        long removed = volunteerActivityMapper.removeVolunteerActivity(id, VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED, UserContext.getUsername());
        if (removed == 0) {
            throw new CommonException("删除志愿活动失败");
        }
        log.info("删除志愿活动成功");
    }

    /**
     * 更新志愿活动
     * @param volunteerActivityDTO 志愿活动信息
     */
    @Override
    @Transactional
    public void updateVolunteerActivity(VolunteerActivityDTO volunteerActivityDTO) throws CommonException {
        log.info("更新志愿活动信息:{}", volunteerActivityDTO);
        // 1. 参数校验
        Long id = volunteerActivityDTO.getId();
        if (id == null || id <= 0) {
            throw new CommonException("更新志愿活动id不能为空");
        }
        validateVolunteerActivity(volunteerActivityDTO);

        // 2. 查询志愿活动信息
        VolunteerActivity activity = volunteerActivityMapper.queryVolunteerActivityById(id, VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
        if (activity == null) {
            throw new CommonException("志愿活动信息不存在");
        }

        // 3. 查询更新的活动名称是否已存在
        VolunteerActivity existingActivity = volunteerActivityMapper.queryVolunteerActivityByActivityName(volunteerActivityDTO.getActivityName(), VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
        if (existingActivity != null && !existingActivity.getId().equals(volunteerActivityDTO.getId())) {
            throw new CommonException("活动名称已存在");
        }

        // 4. 判断活动状态是否变更为已完成，如果是则发放积分
        boolean isCompletingActivity = 
            !VolunteerActivityCommonConstant.ACTIVITY_STATUS_COMPLETED.equals(activity.getStatus()) 
            && VolunteerActivityCommonConstant.ACTIVITY_STATUS_COMPLETED.equals(volunteerActivityDTO.getStatus());
        
        if (isCompletingActivity) {
            log.info("活动状态变更为已完成，准备发放积分：activityId={}, activityName={}", 
                activity.getId(), activity.getActivityName());
            
            // 调用积分发放方法
            distributePointsToParticipants(activity.getId(), activity.getVolunteerHours());
        }

        // 5. 更新志愿活动
        volunteerActivityDTO.setUpdateUser(UserContext.getUsername());
        volunteerActivityDTO.setUpdateTime(new Date());
        long updated = volunteerActivityMapper.updateVolunteerActivity(volunteerActivityDTO);
        if (updated == 0) {
            throw new CommonException("更新志愿活动信息失败");
        }
        log.info("更新志愿活动信息成功");
    }

    /**
     * 为活动的所有正常参与者发放积分、更新活动次数和志愿时长
     * @param activityId 活动 ID
     * @param volunteerHours 志愿时长
     */
    @Transactional(rollbackFor = Exception.class)
    public void distributePointsToParticipants(Long activityId, Double volunteerHours) {
        log.info("开始发放积分、更新活动次数和志愿时长：activityId={}, volunteerHours={}", activityId, volunteerHours);
        
        // 1. 参数校验
        if (activityId == null || activityId <= 0) {
            throw new CommonException("活动 ID 不能为空");
        }
        
        if (volunteerHours == null || volunteerHours <= 0) {
            log.warn("活动志愿时长无效：activityId={}, hours={}", activityId, volunteerHours);
            return;
        }
        
        // 2. 计算积分（时长 × 10）
        Integer pointsPerPerson = (int) Math.round(volunteerHours * 10);
        log.info("计算积分：hours={}, pointsPerPerson={}", volunteerHours, pointsPerPerson);
        
        // 3. 查询所有正常参与的参与者
        List<ActivityParticipant> participants = volunteerActivityMapper.queryNormalParticipantsByActivityId(activityId);
        if (participants == null || participants.isEmpty()) {
            log.warn("活动没有正常参与者：activityId={}", activityId);
            return;
        }
        
        log.info("找到 {} 名正常参与者，开始发放积分并更新统计数据", participants.size());
        
        // 4. 批量处理：发放积分、更新活动次数和志愿时长
        int successCount = 0;
        int failCount = 0;
        
        for (ActivityParticipant participant : participants) {
            try {
                // 4.1 通过 Feign 客户端调用 user-service 增加积分
                ResultInfo<Void> addPointsResult = volunteerServiceClient.addPoints(
                    participant.getUserId(), 
                    pointsPerPerson
                );
                
                // 检查积分发放结果
                if (addPointsResult == null || !"1".equals(addPointsResult.getCode())) {
                    failCount++;
                    log.error("发放积分失败：userId={}, activityId={}, message={}", 
                        participant.getUserId(), activityId, 
                        addPointsResult != null ? addPointsResult.getMsg() : "unknown error");
                    continue; // 积分发放失败，跳过后续操作
                }
                
                // 4.2 更新志愿者的活动次数（+1）
                int updatedActivityCount = volunteerActivityMapper.incrementActivityCount(
                    participant.getUserId(), 
                    UserContext.getUsername()
                );
                if (updatedActivityCount == 0) {
                    failCount++;
                    log.error("更新活动次数失败：userId={}, activityId={}", 
                        participant.getUserId(), activityId);
                    continue;
                }
                
                // 4.3 更新志愿者的累计志愿时长
                int updatedTotalHours = volunteerActivityMapper.incrementTotalHours(
                    participant.getUserId(), 
                    volunteerHours, 
                    UserContext.getUsername()
                );
                if (updatedTotalHours == 0) {
                    failCount++;
                    log.error("更新志愿时长失败：userId={}, activityId={}, hours={}", 
                        participant.getUserId(), activityId, volunteerHours);
                    continue;
                }
                
                // 所有操作成功
                successCount++;
                log.info("发放积分并更新统计成功：userId={}, activityId={}, points={}, activityCount +1, totalHours +{}", 
                    participant.getUserId(), activityId, pointsPerPerson, volunteerHours);
                    
            } catch (Exception e) {
                failCount++;
                log.error("处理异常：userId={}, activityId={}, points={}", 
                    participant.getUserId(), activityId, pointsPerPerson, e);
             }
        }
        
        log.info("完成处理：activityId={}, 应发人数={}, 成功人数={}, 失败人数={}", 
            activityId, participants.size(), successCount, failCount);
        
        // 如果有失败的记录，抛出异常回滚事务
        if (failCount > 0) {
            throw new CommonException("部分处理失败，成功：" + successCount + ", 失败：" + failCount);
        }
    }

    /**
     * 查询志愿活动列表
     * @param pageQueryListDTO 分页查询参数
     * @return 志愿活动列表
     */
    @Override
    public PageInfo queryVolunteerActivityList(PageQueryListDTO pageQueryListDTO) {
        log.info("查询志愿活动列表:{}", pageQueryListDTO);
        // 分页查询
        Page<Object> page = PageHelper.startPage(pageQueryListDTO.getPageNum(), pageQueryListDTO.getPageSize());
        List<VolunteerActivity> activities = volunteerActivityMapper.queryVolunteerActivityList(pageQueryListDTO);
        com.github.pagehelper.PageInfo<VolunteerActivity> pageInfo = new com.github.pagehelper.PageInfo<>(activities);
        log.info("查询志愿活动列表成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 加入志愿活动
     * @param id 志愿活动 id
     */
    @Override
    public void joinActivity(Long id) {
        log.info("加入志愿活动:id-{}", id);

        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new CommonException("志愿活动 id 不能为空");
        }

        // 2. 获取当前用户 ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new CommonException("用户未登录");
        }

        // 3. 查询志愿活动是否存在
        VolunteerActivity activity = volunteerActivityMapper.queryVolunteerActivityById(id, VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
        if (activity == null) {
            throw new CommonException("志愿活动不存在");
        }

        // 4. 检查活动状态是否为报名中
        if (!VolunteerActivityCommonConstant.ACTIVITY_STATUS_ENROLLING.equals(activity.getStatus())) {
            throw new CommonException("活动不在报名中");
        }

        // 5. 检查是否已满员
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new CommonException("活动已满员");
        }

        // 6. 查询用户是否有过参与记录（包括已删除的）
        ActivityParticipant existingParticipant = volunteerActivityMapper.queryParticipantByActivityAndUserAll(id, userId);
        if (existingParticipant != null) {
            if (VolunteerActivityCommonConstant.PARTICIPANT_STATUS_NORMAL.equals(existingParticipant.getStatus())) {
                // 已经是正常参与状态
                throw new CommonException("您已参加过该活动");
            } else if (VolunteerActivityCommonConstant.PARTICIPANT_STATUS_DELETED.equals(existingParticipant.getStatus())) {
                // 用户之前删除过，现在重新报名，恢复其状态为正常
                int updated = volunteerActivityMapper.updateParticipantStatus(
                        existingParticipant.getId(),
                        VolunteerActivityCommonConstant.PARTICIPANT_STATUS_NORMAL,
                        UserContext.getUsername()
                );
                if (updated == 0) {
                    throw new CommonException("重新报名失败，请稍后重试");
                }
    
                // 更新活动参与人数
                int incremented = volunteerActivityMapper.incrementParticipantCount(id, UserContext.getUsername());
                if (incremented == 0) {
                    throw new CommonException("重新报名失败，请稍后重试");
                }
    
                log.info("重新加入志愿活动成功：活动 id={}, 用户 id={}", id, userId);
                return;
            }
        }
            
        // 7. 没有历史记录，创建新的参与者记录
        ActivityParticipant participant = new ActivityParticipant();
        participant.setActivityId(id);
        participant.setUserId(userId);
        participant.setJoinTime(new Date());
        participant.setStatus(VolunteerActivityCommonConstant.PARTICIPANT_STATUS_NORMAL);
        participant.setCreateTime(new Date());
        participant.setUpdateTime(new Date());
        participant.setCreateUser(UserContext.getUsername());
        participant.setUpdateUser(UserContext.getUsername());
        volunteerActivityMapper.insertParticipant(participant);
            
        // 8. 更新活动参与人数
        int updated = volunteerActivityMapper.incrementParticipantCount(id, UserContext.getUsername());
        if (updated == 0) {
            throw new CommonException("加入活动失败，请稍后重试");
        }
            
        log.info("加入志愿活动成功：活动 id={}, 用户 id={}", id, userId);
    }

    /**
     * 取消志愿活动
     * @param id 志愿活动 id
     */
    @Override
    public void cancelActivity(Long id) {
        log.info("取消志愿活动:id-{}", id);
        
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new CommonException("志愿活动 id 不能为空");
        }
        
        // 2. 获取当前用户 ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new CommonException("用户未登录");
        }
        
        // 3. 查询志愿活动是否存在
        VolunteerActivity activity = volunteerActivityMapper.queryVolunteerActivityById(id, VolunteerActivityCommonConstant.ACTIVITY_STATUS_DELETED);
        if (activity == null) {
            throw new CommonException("志愿活动不存在");
        }
        
        // 4. 查询用户是否已报名（只查询正常参与的记录）
        ActivityParticipant participant = volunteerActivityMapper.queryParticipantByActivityAndUser(id, userId);
        if (participant == null) {
            throw new CommonException("您未参加过该活动");
        }
        
        // 5. 检查活动状态（只能是报名中或进行中的活动可以取消）
        String activityStatus = activity.getStatus();
        if (!VolunteerActivityCommonConstant.ACTIVITY_STATUS_ENROLLING.equals(activityStatus) 
            && !VolunteerActivityCommonConstant.ACTIVITY_STATUS_IN_PROGRESS.equals(activityStatus)) {
            throw new CommonException("当前活动状态不允许取消");
        }
        
        // 6. 删除参与者记录（设置为已删除状态）
        int updatedParticipant = volunteerActivityMapper.updateParticipantStatus(
            participant.getId(), 
            VolunteerActivityCommonConstant.PARTICIPANT_STATUS_DELETED, 
            UserContext.getUsername()
        );
        if (updatedParticipant == 0) {
            throw new CommonException("取消活动失败，请稍后重试");
        }
        
        // 7. 减少活动参与人数
        int updatedCount = volunteerActivityMapper.decrementParticipantCount(id, UserContext.getUsername());
        if (updatedCount == 0) {
            throw new CommonException("取消活动失败，请稍后重试");
        }
        
        log.info("取消志愿活动成功：活动 id={}, 用户 id={}", id, userId);
    }

    /**
     * 查询当前用户的所有志愿活动
     * @param pageQueryListDTO 分页查询参数
     * @return 我的活动列表
     */
    @Override
    public PageInfo queryMyActivities(PageQueryListDTO pageQueryListDTO) {
        log.info("查询当前用户的志愿活动列表:{}", pageQueryListDTO);

        Page<Object> page = PageHelper.startPage(pageQueryListDTO.getPageNum(), pageQueryListDTO.getPageSize());
        List<VolunteerActivity> activities = volunteerActivityMapper.queryMyActivities(UserContext.getUserId(), pageQueryListDTO.getStatus());
        com.github.pagehelper.PageInfo<VolunteerActivity> pageInfo = new com.github.pagehelper.PageInfo<>(activities);
        
        log.info("查询当前用户的志愿活动列表成功，用户 id:{}, 总数:{}", UserContext.getUserId(), pageInfo.getTotal());
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }
}