package com.hutb.shopping.model.DTO;

import lombok.Data;

import java.util.Date;

/**
 * 库存数据传输对象
 */
@Data
public class StockDTO {
    private Long id;

    //商品名称
    private String productName;

    //商品描述
    private String productDescription;

    //商品积分价格
    private Double price;

    //商品图片
    private String image;

    //库存数量
    private Integer quantity;

    //商品分类ID
    private Long categoryId;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    private String createUser;

    private String updateUser;

    //状态：1-正常，0-缺货，-1-删除，2-下架
    private String status;
}