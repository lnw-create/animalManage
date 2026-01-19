package com.hutb.shopping.model.DTO;

import lombok.Data;

/**
 * 分类信息DTO
 */
@Data
public class CategoryDTO {
    private Long id;
    
    // 分类名称
    private String name;
    
    // 分类描述
    private String description;

    // 排序值
    private Integer sort;
    
    // 状态：1-正常，0-禁用，-1-删除
    private String status;
    
    // 创建时间
    private java.util.Date createTime;
    
    // 更新时间
    private java.util.Date updateTime;
    
    // 创建人
    private String createUser;
    
    // 更新人
    private String updateUser;
}