# 订单管理功能API规范

## 1. 接口概述

本API规范定义了订单管理功能的所有接口，包括订单的增删改查操作以及销量统计功能。

## 2. 通用约定

### 2.1 HTTP状态码
- 200: 请求成功
- 201: 创建成功
- 400: 请求参数错误
- 401: 未授权
- 403: 禁止访问
- 404: 资源不存在
- 500: 服务器内部错误

### 2.2 数据格式
- 请求头: Content-Type: application/json
- 字符编码: UTF-8

## 3. 订单管理接口

### 3.1 创建订单
- **接口路径**: POST /api/orders
- **接口描述**: 创建一个新的订单
- **请求参数**:
  ```json
  {
    "userId": 123,
    "totalIntegral": 100,
    "shippingAddress": "北京市朝阳区xxx街道",
    "status": "pending_payment"
  }
  ```
- **请求参数说明**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | userId | Long | 是 | 用户ID |
  | totalIntegral | Integer | 是 | 订单总积分 |
  | shippingAddress | String | 是 | 收货地址 |
  | status | String | 否 | 订单状态，默认为pending_payment |

- **响应示例**:
  ```json
  {
    "code": 201,
    "message": "订单创建成功",
    "data": {
      "orderId": 123456789,
      "orderNumber": "ORD2023121200001",
      "userId": 123,
      "totalIntegral": 100,
      "status": "pending_payment",
      "createTime": "2023-12-12T10:30:00",
      "shippingAddress": "北京市朝阳区xxx街道"
    }
  }
  ```

### 3.2 获取订单详情
- **接口路径**: GET /api/orders/{id}
- **接口描述**: 根据订单ID获取订单详情
- **路径参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | id | Long | 是 | 订单ID |

- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "orderId": 123456789,
      "orderNumber": "ORD2023121200001",
      "userId": 123,
      "totalIntegral": 100,
      "status": "pending_payment",
      "createTime": "2023-12-12T10:30:00",
      "updateTime": "2023-12-12T10:30:00",
      "shippingAddress": "北京市朝阳区xxx街道"
    }
  }
  ```

### 3.3 更新订单
- **接口路径**: PUT /api/orders/{id}
- **接口描述**: 更新订单信息
- **路径参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | id | Long | 是 | 订单ID |

- **请求参数**:
  ```json
  {
    "status": "paid",
    "shippingAddress": "北京市海淀区xxx街道"
  }
  ```
- **请求参数说明**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | status | String | 否 | 订单状态 |
  | shippingAddress | String | 否 | 收货地址 |

- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "订单更新成功"
  }
  ```

### 3.4 删除订单
- **接口路径**: DELETE /api/orders/{id}
- **接口描述**: 删除指定订单（软删除）
- **路径参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | id | Long | 是 | 订单ID |

- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "订单删除成功"
  }
  ```

### 3.5 分页查询订单列表
- **接口路径**: GET /api/orders
- **接口描述**: 分页查询订单列表
- **查询参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | userId | Long | 否 | 用户ID |
  | status | String | 否 | 订单状态 |
  | pageNum | Integer | 否 | 页码，默认1 |
  | pageSize | Integer | 否 | 每页大小，默认10 |
  | startTime | String | 否 | 开始时间，格式：yyyy-MM-dd HH:mm:ss |
  | endTime | String | 否 | 结束时间，格式：yyyy-MM-dd HH:mm:ss |

- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "total": 100,
      "pageNum": 1,
      "pageSize": 10,
      "list": [
        {
          "orderId": 123456789,
          "orderNumber": "ORD2023121200001",
          "userId": 123,
          "totalIntegral": 100,
          "status": "paid",
          "createTime": "2023-12-12T10:30:00",
          "shippingAddress": "北京市朝阳区xxx街道"
        }
      ]
    }
  }
  ```

## 4. 销量统计接口

### 4.1 查询某时间段内销量前10的商品
- **接口路径**: GET /api/orders/sales-top10
- **接口描述**: 查询某时间段内销量前10的商品
- **查询参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | startTime | String | 是 | 开始时间，格式：yyyy-MM-dd HH:mm:ss |
  | endTime | String | 是 | 结束时间，格式：yyyy-MM-dd HH:mm:ss |

- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "productId": 1,
        "productName": "宠物食品",
        "salesQuantity": 150
      },
      {
        "productId": 2,
        "productName": "宠物玩具",
        "salesQuantity": 120
      }
    ]
  }
  ```

## 5. 数据模型

### 5.1 订单状态枚举
- pending_payment: 待付款
- paid: 已付款
- shipped: 已发货
- delivered: 已签收
- cancelled: 已取消
- refunded: 已退款

### 5.2 通用响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```