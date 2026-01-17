# shopping-service 时间戳和用户字段统一规范

## 实体类变更

### Stock.java
```java
// shopping-service 中的字段命名已经符合规范
private Date createTime;
private Date updateTime;
private String createUser;
private String updateUser;    // 已经是正确的命名
```

## Mapper 接口变更

### StockMapper.java
```java
// shopping-service 中的SQL语句应该已经使用正确的字段名
// 如果发现使用了 modified_user，则需要更改为 update_user
```

## Service 实现变更

### StockServiceImpl.java
```java
// shopping-service 中的字段引用应该已经使用正确的字段名
// 如果发现使用了 modifiedUser，则需要更改为 updateUser
```

## 注意事项

1. shopping-service 中的字段命名基本已经符合规范，只需确认没有遗留的 `modifiedUser` 或 `modified_user` 引用
2. 检查 StockMapper.xml 中是否有需要更新的字段引用