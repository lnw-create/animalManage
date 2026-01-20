# 宠物信息CRUD功能规格说明

## 概述
此规格说明定义了在pet-service中实现宠物信息CRUD功能的需求。该功能将允许系统对宠物信息进行创建、读取、更新和删除操作。

## 功能需求

### 1. 数据模型
宠物信息包含以下字段：
- id: 宠物唯一标识符（Long类型，自动生成）
- name: 宠物名称（String类型，必填）
- species: 宠物种类（如狗、猫、兔子等，String类型，必填）
- breed: 宠物品种（String类型，可选）
- age: 宠物年龄（月龄或岁数，String类型，可选）
- gender: 宠物性别（0-雌性, 1-雄性，String类型，可选）
- healthStatus: 健康状况（String类型，可选）
- isNeutered: 是否已绝育（0-否, 1-是，String类型，可选，默认为"0"）
- isVaccinated: 是否接种疫苗（0-否, 1-是，String类型，可选，默认为"0"）
- adoptionStatus: 领养状态（0-待领养, 1-已申请, 2-已领养，Integer类型，新增时默认为0）
- description: 宠物描述/背景故事（String类型，可选）
- photo: 主图URL（String类型，可选）
- ownerId: 当前主人ID（领养人，Long类型，新增时默认为空）

### 2. 常量定义
在`PetConstant.java`中定义以下常量：
- 领养状态常量：ADOPTION_STATUS_AVAILABLE(0)-待领养, ADOPTION_STATUS_APPLIED(1)-已申请, ADOPTION_STATUS_ADOPTED(2)-已领养
- 绝育状态常量：NEUTERED_NO(0)-未绝育, NEUTERED_YES(1)-已绝育
- 疫苗状态常量：VACCINATED_NO(0)-未接种, VACCINATED_YES(1)-已接种
- 性别常量：GENDER_FEMALE(0)-雌性, GENDER_MALE(1)-雄性

### 3. 验证规则
在`CommonValidate.java`中实现宠物信息验证方法：
- 宠物名称不能为空且长度不超过100字符
- 宠物种类不能为空且长度不超过50字符
- 年龄长度不超过20字符
- 健康状况长度不超过100字符
- 描述长度不超过1000字符
- 照片URL长度不超过500字符
- 性别只能是"0"或"1"
- 绝育状态只能是"0"或"1"
- 疫苗状态只能是"0"或"1"
- 领养状态只能是0、1或2
- 主人ID如果提供则必须大于0

### 4. API接口设计

#### 4.1 新增宠物信息
- 接口路径：POST /pet/addPet
- 请求体：PetDTO对象
- 返回：ResultInfo
- 特殊处理：新增时ownerId默认设置为null，adoptionStatus默认设置为0（待领养）

#### 4.2 删除宠物信息
- 接口路径：POST /pet/removePet
- 请求参数：id (Long类型)
- 返回：ResultInfo

#### 4.3 更新宠物信息
- 接口路径：POST /pet/editPet
- 请求体：PetDTO对象
- 返回：ResultInfo

#### 4.4 查询宠物列表
- 接口路径：GET /pet/queryPetList
- 请求体：PageQueryListDTO对象
- 返回：ResultInfo包含分页数据

### 5. 实现要求

#### 5.1 Controller层
- 使用@RestController注解
- 使用@RequestMapping("pet")映射路径
- 所有方法需要捕获CommonException和其他异常，并返回适当的结果
- 异常处理遵循统一格式

#### 5.2 Service层
- 定义PetService接口及其实现类PetServiceImpl
- 实现完整的CRUD方法
- 包含适当的日志记录
- 在新增和更新操作中设置时间戳和用户信息

#### 5.3 Mapper层
- 定义PetMapper接口
- 提供相应的SQL映射文件
- 支持软删除（通过状态字段）
- 支持分页查询

### 6. 默认值处理
- 新增宠物时，ownerId字段默认设置为null
- 新增宠物时，adoptionStatus字段默认设置为0（待领养状态）
- 创建时间和更新时间自动设置
- 创建人和更新人从UserContext获取

### 7. 参考实现
实现应参考shopping-service中的Category和Stock的CRUD实现模式，保持一致的编码风格和错误处理机制。

### 8. 数据库设计
- 表名：pet
- 字段与Pet实体类对应
- 包含创建时间(create_time)、更新时间(update_time)、创建人(create_user)、更新人(update_user)字段
- 软删除通过状态字段实现