package com.hutb.shopping.model.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 库存实体类
 */
@Data
public class Stock {

    private Long id;

     //商品ID
    private Long productId;

     //商品名称
    private String productName;

    //库存数量
    private Integer quantity;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    private String createUser;

    private String updateUser;

    //状态：1-正常，0-缺货，-1-删除，2-下架
    private String status;
}