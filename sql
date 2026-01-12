create database animal_management;

use animal_management;

-- 管理员表
CREATE TABLE admin (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(255) NOT NULL UNIQUE COMMENT '用户名，不允许重复',
                       password VARCHAR(255) NOT NULL COMMENT '密码',
                       real_name VARCHAR(255) COMMENT '真实姓名',
                       role VARCHAR(20) COMMENT '角色：1表示超级管理员，0表示普通管理员',
                       phone VARCHAR(20) UNIQUE COMMENT '电话号码，不允许重复',
                       email VARCHAR(255) COMMENT '邮箱',
                       status VARCHAR(10) DEFAULT '1' COMMENT '状态：1表示正常，0表示禁用，-1表示删除',
                       create_time DATETIME COMMENT '创建时间',
                       update_time DATETIME COMMENT '更新时间',
                       create_user VARCHAR(255) COMMENT '创建人',
                       modified_user VARCHAR(255) COMMENT '修改人'
) COMMENT '管理员表';


-- 用户表
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(255) NOT NULL UNIQUE COMMENT '用户名，不允许重复',
                      password VARCHAR(255) NOT NULL COMMENT '密码',
                      real_name VARCHAR(255) COMMENT '真实姓名',
                      phone VARCHAR(20) UNIQUE COMMENT '电话号码，不允许重复',
                      email VARCHAR(255) COMMENT '邮箱',
                      status VARCHAR(10) DEFAULT '1' COMMENT '状态：1表示正常，0表示禁用，-1表示删除',
                      create_time DATETIME COMMENT '创建时间',
                      update_time DATETIME COMMENT '更新时间',
                      create_user VARCHAR(255) COMMENT '创建人',
                      modified_user VARCHAR(255) COMMENT '修改人'
) COMMENT '用户表';

CREATE TABLE volunteer (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           user_id BIGINT,
                           username VARCHAR(255) NOT NULL UNIQUE,
                           real_name VARCHAR(255) NOT NULL,
                           id_card VARCHAR(50),
                           phone VARCHAR(20) UNIQUE,
                           address VARCHAR(255),
                           total_hours DOUBLE DEFAULT 0.0,
                           activity_count INT DEFAULT 0,
                           status VARCHAR(10) DEFAULT '1', -- '1'表示正常，'0'表示禁用，'-1'表示删除
                           create_time DATETIME,
                           update_time DATETIME,
                           create_user VARCHAR(255),
                           modified_user VARCHAR(255)
)comment '志愿者表';

CREATE TABLE activity (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL UNIQUE COMMENT '活动名称',
                          description TEXT COMMENT '活动描述',
                          status VARCHAR(20) COMMENT '活动状态（报名中/进行中/已结束）',
                          location VARCHAR(255) COMMENT '活动地点',
                          max_participants INT DEFAULT 0 COMMENT '最大参与人数',
                          current_participants INT DEFAULT 0 COMMENT '当前报名人数',
                          volunteer_hours INT COMMENT '志愿时长',
                          start_time DATETIME COMMENT '开始时间',
                          end_time DATETIME COMMENT '结束时间',
                          create_time DATETIME COMMENT '创建时间',
                          update_time DATETIME COMMENT '更新时间',
                          create_user VARCHAR(255) COMMENT '创建人',
                          modified_user VARCHAR(255) COMMENT '修改人'
) COMMENT '活动表';