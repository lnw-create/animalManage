create database animal_management;

use animal_management;

-- 管理员表
CREATE TABLE admin (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(255) NOT NULL UNIQUE COMMENT '用户名，不允许重复',
                       password VARCHAR(255) NOT NULL COMMENT '密码',
                       real_name VARCHAR(255) COMMENT '真实姓名',
                       role VARCHAR(20) COMMENT '角色',
                       phone VARCHAR(20) UNIQUE COMMENT '电话号码，不允许重复',
                       gender VARCHAR(10) COMMENT '性别',
                       id_card VARCHAR(50) COMMENT '身份证号',
                       status VARCHAR(10) DEFAULT '1' COMMENT '状态：1表示正常，0表示禁用，-1表示删除',
                       create_time DATETIME COMMENT '创建时间',
                       update_time DATETIME COMMENT '更新时间',
                       create_user VARCHAR(255) COMMENT '创建人',
                       update_user VARCHAR(255) COMMENT '修改人'
) COMMENT '管理员表';


-- 用户表
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(255) NOT NULL UNIQUE COMMENT '用户名，不允许重复',
                      password VARCHAR(255) NOT NULL COMMENT '密码',
                      real_name VARCHAR(255) COMMENT '真实姓名',
                      role VARCHAR(20) COMMENT '角色',
                      phone VARCHAR(20) UNIQUE COMMENT '电话号码，不允许重复',
                      gender VARCHAR(10) COMMENT '性别',
                      id_card VARCHAR(50) COMMENT '身份证号',
                      status VARCHAR(10) DEFAULT '1' COMMENT '状态：1表示正常，0表示禁用，-1表示删除',
                      create_time DATETIME COMMENT '创建时间',
                      update_time DATETIME COMMENT '更新时间',
                      create_user VARCHAR(255) COMMENT '创建人',
                      update_user VARCHAR(255) COMMENT '修改人'
) COMMENT '用户表';

CREATE TABLE volunteer (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           user_id BIGINT,
                           username VARCHAR(255) NOT NULL UNIQUE,
                           real_name VARCHAR(255) NOT NULL,
                           id_card VARCHAR(50),
                           phone VARCHAR(20) UNIQUE,
                           gender VARCHAR(10),
                           address VARCHAR(255),
                           total_hours DOUBLE DEFAULT 0.0,
                           activity_count INT DEFAULT 0,
                           activity_point INT DEFAULT 0,
                           status VARCHAR(10) DEFAULT '1', -- '1'表示正常，'0'表示禁用，'-1'表示删除
                           create_time DATETIME,
                           update_time DATETIME,
                           create_user VARCHAR(255),
                           update_user VARCHAR(255)
)comment '志愿者表';

CREATE TABLE activity (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          activity_name VARCHAR(255) NOT NULL UNIQUE COMMENT '活动名称',
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
                          update_user VARCHAR(255) COMMENT '修改人'
) COMMENT '活动表';

CREATE TABLE stock (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                       product_name VARCHAR(255) NOT NULL UNIQUE COMMENT '商品名称',
                       product_description TEXT COMMENT '商品描述',
                       price DECIMAL(10,2) COMMENT '商品积分价格',
                       image VARCHAR(500) COMMENT '商品图片',
                       quantity INT DEFAULT 0 COMMENT '库存数量',
                       category_id BIGINT COMMENT '商品分类ID',
                       create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                       create_user VARCHAR(50) COMMENT '创建人',
                       update_user VARCHAR(50) COMMENT '更新人',
                       status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-缺货，-1-删除，2-下架'
) COMMENT='库存表';

CREATE TABLE category (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                          name VARCHAR(100) NOT NULL UNIQUE COMMENT '分类名称',
                          description VARCHAR(500) COMMENT '分类描述',
                          sort INT DEFAULT 0 COMMENT '排序值',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          create_user VARCHAR(50) COMMENT '创建人',
                          update_user VARCHAR(50) COMMENT '更新人',
                          status VARCHAR(2) DEFAULT '1' COMMENT '状态：1-正常，0-禁用，-1-删除'
) COMMENT='商品分类表';

CREATE TABLE pet (
                     id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '宠物ID',
                     name VARCHAR(255) NOT NULL COMMENT '宠物名称',
                     species VARCHAR(100) NOT NULL COMMENT '宠物种类（如狗、猫、兔子等）',
                     breed VARCHAR(100) COMMENT '宠物品种',
                     age VARCHAR(20) COMMENT '宠物年龄（月龄或岁数）',
                     gender VARCHAR(10) COMMENT '宠物性别 (0-雌性, 1-雄性)',
                     health_status VARCHAR(255) COMMENT '健康状况',
                     is_neutered VARCHAR(10) DEFAULT '0' COMMENT '是否已绝育 (0-否, 1-是)',
                     is_vaccinated VARCHAR(10) DEFAULT '0' COMMENT '是否接种疫苗 (0-否, 1-是)',
                     adoption_status INT DEFAULT 0 COMMENT '领养状态 (0-待领养, 1-已申请, 2-已领养)',
                     description TEXT COMMENT '宠物描述/背景故事',
                     photo VARCHAR(500) COMMENT '主图URL',
                     owner_id BIGINT COMMENT '当前主人ID（领养人）',
                     create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                     create_user VARCHAR(100) COMMENT '创建人',
                     update_user VARCHAR(100) COMMENT '更新人'
) COMMENT='宠物表';


CREATE TABLE adoption_application (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
                                      pet_id BIGINT NOT NULL COMMENT '宠物ID',
                                      user_id BIGINT NOT NULL COMMENT '申请人ID',
                                      status VARCHAR(20) DEFAULT 'PENDING' COMMENT '申请状态: PENDING(待审批), APPROVED(已批准), REJECTED(已拒绝)',
                                      applicant_name VARCHAR(100) COMMENT '申请人姓名',
                                      applicant_phone VARCHAR(20) COMMENT '申请人电话',
                                      applicant_address TEXT COMMENT '申请人地址',
                                      application_reason TEXT COMMENT '申请理由',
                                      create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      create_user VARCHAR(50) COMMENT '创建人',
                                      update_user VARCHAR(50) COMMENT '更新人',
                                      INDEX idx_pet_id (pet_id),
                                      INDEX idx_user_id (user_id),
                                      INDEX idx_status (status)
) COMMENT='领养申请表';

CREATE TABLE orders (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
                        user_id BIGINT NOT NULL COMMENT '用户ID',
                        order_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
                        total_integral INT NOT NULL COMMENT '订单总积分',
                        status VARCHAR(20) NOT NULL DEFAULT 'pending_payment' COMMENT '订单状态',
                        create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        create_user VARCHAR(50) COMMENT '创建人',
                        update_user VARCHAR(50) COMMENT '更新人',
                        shipping_address VARCHAR(200) COMMENT '收货地址',
                        product_id BIGINT COMMENT '商品ID',
                        product_name VARCHAR(100) COMMENT '商品名称',
                        price INT COMMENT '单价（积分）',

                        INDEX idx_user_id (user_id),
                        INDEX idx_order_number (order_number),
                        INDEX idx_status (status),
                        INDEX idx_create_time (create_time),
                        INDEX idx_product_id (product_id)
) COMMENT='订单表';
