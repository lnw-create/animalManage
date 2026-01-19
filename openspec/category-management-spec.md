                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 # 商品分类管理功能规格变更

## 变更概述
本次变更在shopping-service中新增商品分类管理功能，包括基本的CRUD操作。

## 功能范围
- 商品分类信息的增删改查
- 分类列表分页查询
- 分类名称唯一性验证

## 实体设计
### Category实体
- id: 主键，自增长
- name: 分类名称，非空，唯一
- description: 分类描述，可选
- parentId: 父级分类ID，用于构建层级结构（可选）
- sort: 排序值，默认为0
- createTime: 创建时间
- updateTime: 更新时间
- isDeleted: 删除标识

## API接口设计
### POST /api/category/create
创建新的商品分类

### GET /api/category/{id}
根据ID获取分类详情

### PUT /api/category/update
更新分类信息

### DELETE /api/category/delete/{id}
删除分类（软删除）

### GET /api/category/list
分页查询分类列表

### GET /api/category/tree
获取分类树形结构

## 数据库表设计
表名：category
字段：
- id: BIGINT PRIMARY KEY AUTO_INCREMENT
- name: VARCHAR(100) NOT NULL UNIQUE
- description: VARCHAR(500)
- parent_id: BIGINT DEFAULT 0
- sort: INT DEFAULT 0
- create_time: DATETIME
- update_time: DATETIME
- is_deleted: TINYINT DEFAULT 0
