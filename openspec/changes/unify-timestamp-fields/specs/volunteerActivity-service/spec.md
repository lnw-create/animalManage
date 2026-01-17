# volunteerActivity-service 时间戳和用户字段统一规范

## 实体类变更

### VolunteerActivityDTO.java
```java
// 变更前
private Date createTime;
private Date updateTime;
private String createUser;
private String modifiedUser;  // 需要重命名为 updateUser

// 变更后
private Date createTime;
private Date updateTime;
private String createUser;
private String updateUser;    // 已重命名
```

## Mapper 接口变更

### volunteerActivityMapper.java
```java
// SQL语句变更前
@Insert("insert into activity(activity_name, description, start_time, end_time, location, max_participants, current_participants, volunteer_hours, status, create_time, update_time, create_user, modified_user) " +
        "values(#{activityName}, #{description}, #{startTime}, #{endTime}, #{location}, #{maxParticipants}, #{currentParticipants},#{volunteerHours}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{modifiedUser})")

// SQL语句变更后
@Insert("insert into activity(activity_name, description, start_time, end_time, location, max_participants, current_participants, volunteer_hours, status, create_time, update_time, create_user, update_user) " +
        "values(#{activityName}, #{description}, #{startTime}, #{endTime}, #{location}, #{maxParticipants}, #{currentParticipants},#{volunteerHours}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")

// 其他SQL语句中也需要做相应修改
```

## Service 实现变更

### VolunteerActivityServiceImpl.java
```java
// 字段设置变更前
volunteerActivityDTO.setModifiedUser(UserContext.getUsername());

// 字段设置变更后
volunteerActivityDTO.setUpdateUser(UserContext.getUsername());
```

## 注意事项

1. 确保所有对 `modifiedUser` 的引用都替换为 `updateUser`
2. 更新相关的 getter 和 setter 方法
3. 验证数据库映射关系
4. 检查所有SQL语句中的字段引用