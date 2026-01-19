package com.hutb.shopping.model.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 商品分类实体类
 */
@Data
public class Category {
    private Long id;
    
    // 分类名称
    private String name;
    
    // 分类描述
    private String description;

    // 排序值
    private Integer sort;
    
    // 创建时间
    private Date createTime;
    
    // 更新时间
    private Date updateTime;
    
    // 创建人
    private String createUser;
    
    // 更新人
    private String updateUser;
    
    // 状态：1-正常，0-禁用，-1-删除
    private String status;
}