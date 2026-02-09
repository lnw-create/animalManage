# 订单管理功能设计方案

## 1. 整体架构

采用经典的分层架构模式，包括：
- 控制层(Controller)：负责接收HTTP请求并返回响应
- 服务层(Service)：处理业务逻辑
- 数据访问层(Mapper)：负责与数据库交互
- 数据传输对象(DTO)：用于数据传递

## 2. 实体设计

### 2.1 订单实体(Order)
当前系统中已存在Order实体，包含以下字段：
- orderId: 订单ID(主键)
- userId: 用户ID
- orderNumber: 订单号
- totalIntegral: 订单消耗的积分
- status: 订单状态
- createTime: 创建时间
- updateTime: 更新时间
- shippingAddress: 收货地址

### 2.2 订单创建DTO
新增OrderCreateDTO，用于创建订单时的数据传输：
- userId: 用户ID(必填)
- totalIntegral: 积分总额(必填)
- shippingAddress: 收货地址(必填)
- status: 订单状态(选填，默认为待支付)

### 2.3 订单更新DTO
新增OrderUpdateDTO，用于更新订单时的数据传输：
- orderId: 订单ID(必填)
- status: 订单状态(选填)
- shippingAddress: 收货地址(选填)

### 2.4 订单查询DTO
新增OrderQueryDTO，用于订单查询时的数据传输：
- orderId: 订单ID
- userId: 用户ID
- status: 订单状态
- startTime: 创建开始时间
- endTime: 创建结束时间

## 3. 接口设计

### 3.1 订单控制器(ShoppingController)
- POST /api/orders: 创建订单
- GET /api/orders/{id}: 根据ID获取订单详情
- PUT /api/orders/{id}: 更新订单信息
- DELETE /api/orders/{id}: 删除订单
- GET /api/orders: 分页查询订单列表
- GET /api/orders/sales-top10: 查询某时间段内销量前10的商品

### 3.2 数据访问层
- OrderMapper: 处理订单相关数据库操作
- OrderMapper.xml: SQL语句定义

### 3.3 服务层
- OrderService: 定义订单业务接口
- OrderServiceImpl: 实现订单业务逻辑

## 4. 数据库设计

当前系统中应存在orders表，包含以下字段：
- id: 主键ID
- user_id: 用户ID
- order_number: 订单号
- total_integral: 总积分
- status: 订单状态
- create_time: 创建时间
- update_time: 更新时间
- shipping_address: 收货地址

## 5. 销量统计功能设计

新增销量统计方法，根据订单记录统计某时间段内销量前10的商品信息：
- 输入参数：开始时间、结束时间
- 输出结果：商品ID、商品名称、销售数量(按销量降序排列，取前10)

## 6. 业务流程

### 6.1 创建订单流程
1. 接收订单创建请求
2. 验证参数有效性
3. 生成订单号
4. 保存订单信息
5. 返回订单创建结果

### 6.2 更新订单流程
1. 接收订单更新请求
2. 验证订单存在性
3. 更新订单信息
4. 返回更新结果

### 6.3 销量统计流程
1. 接收销量统计请求
2. 构建查询条件(时间范围)
3. 执行销量统计SQL
4. 返回销量排名结果

## 7. 异常处理

- 订单不存在异常
- 参数验证异常
- 数据库操作异常
- 权限不足异常

## 8. 安全考虑

- 对用户ID进行身份验证，确保只能操作自己的订单
- 对敏感操作进行权限校验
- 防止SQL注入攻击