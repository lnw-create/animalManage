# 库存服务API规范

## 实体定义

### Stock (库存实体)

| 字段名 | 类型 | 描述 | 是否必填 |
|--------|------|------|----------|
| id | Long | 库存ID | 否(新增时无需提供) |
| productId | Long | 商品ID | 是 |
| productName | String | 商品名称 | 是 |
| quantity | Integer | 库存数量 | 是 |
| createTime | Long | 创建时间 | 否(系统自动生成) |
| updateTime | Long | 更新时间 | 否(系统自动生成) |
| status | Integer | 状态：1-正常，0-缺货，-1-删除，2-下架 | 是 |

## API接口定义

### 1. 新增库存
- **接口路径**: POST /stock/addStock
- **请求参数**: Stock对象
- **返回结果**: ResultInfo
- **描述**: 添加新的库存记录

### 2. 删除库存
- **接口路径**: POST /stock/removeStock
- **请求参数**: id (Long类型，通过@RequestParam传递)
- **返回结果**: ResultInfo
- **描述**: 根据ID删除库存记录(软删除，将状态设为-1)

### 3. 更新库存
- **接口路径**: POST /stock/editStock
- **请求参数**: Stock对象
- **返回结果**: ResultInfo
- **描述**: 更新库存信息

### 4. 查询库存列表
- **接口路径**: GET /stock/queryStockList
- **请求参数**: PageQueryListDTO对象
- **返回结果**: ResultInfo<PageInfo>
- **描述**: 分页查询库存列表

## 异常处理
- 所有接口应按照统一异常处理机制返回ResultInfo格式的错误信息
- 业务异常需抛出CommonException，系统异常需记录日志并返回通用错误信息

## 数据验证
- 商品ID、商品名称、库存数量等字段必须进行非空验证
- 库存数量不能为负数
- 状态值应在指定范围内(1, 0, -1, 2)