# 志愿活动服务CRUD功能设计文档

## 概述

本文档详细描述志愿活动服务CRUD功能的技术设计方案，包括实体类设计、API接口设计、数据访问层设计等。

## 实体类设计

基于常见的志愿活动业务场景，设计VolunteerActivity实体类，包含以下字段：
- id: 主键
- activityName: 活动名称
- activityDescription: 活动描述
- startTime: 开始时间
- endTime: 结束时间
- location: 活动地点
- organizer: 组织者
- maxParticipants: 最大参与者数量
- currentParticipants: 当前参与者数量
- status: 活动状态
- createTime: 创建时间
- updateTime: 更新时间
- createUser: 创建用户
- modifiedUser: 修改用户

## API接口设计

### 1. 新增志愿活动
- 接口：POST /admin/volunteerActivity/addActivity
- 参数：VolunteerActivityDTO
- 返回：ResultInfo

### 2. 删除志愿活动
- 接口：POST /admin/volunteerActivity/removeActivity
- 参数：id
- 返回：ResultInfo

### 3. 更新志愿活动
- 接口：POST /admin/volunteerActivity/editActivity
- 参数：VolunteerActivityDTO
- 返回：ResultInfo

### 4. 查询志愿活动列表
- 接口：GET /admin/volunteerActivity/queryActivityList
- 参数：PageQueryListDTO
- 返回：ResultInfo<PageInfo>

## 技术架构

遵循现有user-service的架构模式：
- Controller层：处理HTTP请求
- Service层：业务逻辑处理
- Mapper层：数据访问操作
- DTO/VO层：数据传输对象

## 数据库设计

预计需要创建volunteer_activity表，包含上述实体类的所有字段。