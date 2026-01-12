# Change: 合并User和Employee管理

## Why
当前系统中User和Employee是分开管理的，分别有不同的实体、服务和控制器。这种分离导致代码重复、维护困难，且在实际业务场景中User和Employee的界限可能不明确。合并管理可以减少代码重复，提高系统的一致性和可维护性。

## What Changes
- **BREAKING**: 将Admin(员工)实体和User实体合并为统一的User实体，通过角色字段区分用户类型
- **BREAKING**: 统一UserService和EmployeeService为单一的UserService
- **BREAKING**: 统一UserController和EmployeeController为单一的UserController
- **BREAKING**: 数据库表admin和user合并为单一的user表，通过role字段区分用户角色
- 修改相关的DTO、VO、POJO和Mapper

## Impact
- Affected specs: user-service相关功能
- Affected code: 
  - com.hutb.user.service.UserService, com.hutb.user.service.EmployeeService
  - com.hutb.user.controller.UserController, com.hutb.user.controller.EmployeeController
  - com.hutb.user.mapper.userMapper, com.hutb.user.mapper.employeeMapper
  - com.hutb.user.model.pojo.User, com.hutb.user.model.pojo.Admin
  - 相关的DTO和VO类