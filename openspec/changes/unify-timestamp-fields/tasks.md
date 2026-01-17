# 统一时间戳和用户字段命名规范实施任务清单

## 第一阶段：实体类字段变更

### user-service
- [ ] 修改 User.java: 将 `modifiedUser` 字段重命名为 `updateUser`
- [ ] 修改 UserDTO.java: 将 `modifiedUser` 字段重命名为 `updateUser`
- [ ] 修改 VolunteerDTO.java: 将 `modifiedUser` 字段重命名为 `updateUser`
- [ ] 检查并更新其他可能包含 `modifiedUser` 的实体类

### volunteerActivity-service
- [ ] 修改 VolunteerActivityDTO.java: 将 `modifiedUser` 字段重命名为 `updateUser`

## 第二阶段：Mapper 接口变更

### user-service
- [ ] 修改 userMapper.java: 更新SQL语句中的 `modified_user` 为 `update_user`
- [ ] 修改 employeeMapper.java: 如有类似字段也需要更新
- [ ] 修改 volunteerMapper.java: 如有类似字段也需要更新

### volunteerActivity-service
- [ ] 修改 volunteerActivityMapper.java: 更新SQL语句中的 `modified_user` 为 `update_user`

## 第三阶段：Mapper XML 文件变更

### user-service
- [ ] 修改 userMapper.xml: 更新SQL语句中的 `modified_user` 为 `update_user`
- [ ] 修改 employeeMapper.xml: 如有类似字段也需要更新
- [ ] 修改 volunteerMapper.xml: 如有类似字段也需要更新

### shopping-service
- [ ] 修改 StockMapper.xml: 确认字段名称一致性

### volunteerActivity-service
- [ ] 修改 VolunteerActivityMapper.xml: 更新SQL语句中的 `modified_user` 为 `update_user`

## 第四阶段：Service 实现变更

### user-service
- [ ] 修改 UserServiceImpl.java: 更新对 `modifiedUser` 的引用为 `updateUser`
- [ ] 修改 EmployeeServiceImpl.java: 如有类似字段也需要更新
- [ ] 修改 VolunteerServiceImpl.java: 更新对 `modifiedUser` 的引用为 `updateUser`

### shopping-service
- [ ] 修改 StockServiceImpl.java: 确认字段名称一致性

### volunteerActivity-service
- [ ] 修改 VolunteerActivityServiceImpl.java: 更新对 `modifiedUser` 的引用为 `updateUser`

## 第五阶段：测试验证

- [ ] 编译验证：确保所有变更不会导致编译错误
- [ ] 单元测试：运行所有单元测试验证功能正确性
- [ ] 集成测试：验证数据库操作的正确性
- [ ] 手动测试：验证关键业务流程