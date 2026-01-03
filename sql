create database animal_management;

use animal_management;

CREATE TABLE admin (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       real_name VARCHAR(255),
                       role VARCHAR(10), -- '1'表示超级管理员，'0'表示普通管理员
                       phone VARCHAR(20),
                       email VARCHAR(255),
                       status VARCHAR(10) DEFAULT '1', -- '1'表示正常，'0'表示禁用，'-1'表示删除
                       create_time DATETIME,
                       update_time DATETIME,
                       create_user VARCHAR(255),
                       modified_user VARCHAR(255)
)comment '管理员表';


CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(255) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      real_name VARCHAR(255),
                      phone VARCHAR(20),
                      email VARCHAR(255),
                      status VARCHAR(10) DEFAULT '1', -- '1'表示正常，'0'表示禁用，'-1'表示删除
                      create_time DATETIME,
                      update_time DATETIME,
                      create_user VARCHAR(255),
                      modified_user VARCHAR(255)
)comment '用户表';

CREATE TABLE volunteer (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      user_id BIGINT,
                      real_name VARCHAR(255) NOT NULL,
                      gender VARCHAR(10), -- '1'表示男，'2'表示女
                      id_card VARCHAR(50),
                      phone VARCHAR(20),
                      address VARCHAR(255),
                      total_hours DOUBLE DEFAULT 0.0,
                      activity_count INT DEFAULT 0,
                      status VARCHAR(10) DEFAULT '1', -- '1'表示正常，'0'表示禁用，'-1'表示删除
                      create_time DATETIME,
                      update_time DATETIME,
                      create_user VARCHAR(255),
                      modified_user VARCHAR(255)
)comment '志愿者表';
