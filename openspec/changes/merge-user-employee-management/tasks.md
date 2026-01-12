## 1. 数据模型合并
- [ ] 1.1 修改User实体，添加role字段以区分用户类型
- [ ] 1.2 修改UserDTO，添加role字段
- [ ] 1.3 创建统一的用户类型常量定义

## 2. 服务层合并
- [ ] 2.1 合并UserService和EmployeeService接口为统一的UserService
- [ ] 2.2 合并UserServiceImpl和EmployeeServiceImpl为统一的UserServiceImpl
- [ ] 2.3 更新相关业务逻辑以支持角色区分

## 3. 控制器合并
- [ ] 3.1 合并UserController和EmployeeController为统一的UserController
- [ ] 3.2 保留所有原有API端点，可能需要调整URL路径

## 4. 数据访问层合并
- [ ] 4.1 合并userMapper和employeeMapper为统一的userMapper
- [ ] 4.2 更新MyBatis XML映射文件以支持统一的用户管理

## 5. 测试和验证
- [ ] 5.1 更新单元测试
- [ ] 5.2 进行集成测试
- [ ] 5.3 验证所有功能正常工作