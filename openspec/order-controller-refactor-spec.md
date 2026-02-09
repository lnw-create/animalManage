# 订单控制器重构规范

## 1. 重构背景
当前ShoppingController存在以下设计问题：
- 类名与功能不匹配（ShoppingController实际处理订单）
- 接口设计不规范，混合使用GET/POST
- URL路径设计不统一
- 缺少标准RESTful设计

## 2. 重构目标
- 简化控制器设计，遵循项目统一规范
- 采用标准RESTful API设计
- 统一命名规范和接口风格
- 参考其他控制器的编码风格

## 3. 设计变更

### 3.1 类名变更
- **原类名**: ShoppingController
- **新类名**: OrderController
- **理由**: 类名应准确反映控制器处理的业务实体

### 3.2 URL路径规范
- **原路径**: /api/orders/**
- **新路径**: /order/**
- **理由**: 与其他服务保持一致的简洁路径风格

### 3.3 接口设计规范
采用标准RESTful设计：

| 操作 | HTTP方法 | 路径 | 方法名 |
|------|----------|------|--------|
| 创建订单 | POST | /order | createOrder |
| 获取订单 | GET | /order/{id} | getOrder |
| 更新订单 | PUT | /order/{id} | updateOrder |
| 删除订单 | DELETE | /order/{id} | deleteOrder |
| 查询订单列表 | GET | /order/list | getOrderList |
| 销量排行 | GET | /order/sales/top10 | getSalesTop10 |

### 3.4 方法命名规范
- 统一使用动词+名词的命名方式
- 去除冗余的"By"、"ById"等后缀
- 与PetController等保持一致风格

## 4. 实体类优化建议
Order实体类可考虑：
- 统一时间字段命名(createTime, updateTime)
- 规范创建人/更新人字段命名(createUser, updateUser)
- 确保字段注释完整准确

## 5. 实施步骤
1. 重命名ShoppingController为OrderController
2. 调整@RequestMapping路径
3. 统一接口设计风格
4. 规范方法命名
5. 更新相关引用

## 6. 兼容性考虑
- 需要更新前端调用地址
- 网关路由配置需要相应调整
- API文档需要同步更新