package com.hutb.shopping.service;

import com.hutb.shopping.model.DTO.sales.SalesTop10DTO;
import com.hutb.shopping.model.DTO.sales.SalesTop10QueryDTO;
import com.hutb.shopping.model.pojo.Order;
import com.hutb.shopping.model.pojo.PageInfo;
import com.hutb.shopping.model.DTO.order.OrderQueryDTO;
import com.hutb.shopping.model.DTO.order.OrderCreateDTO;
import com.hutb.shopping.model.DTO.order.OrderUpdateDTO;

import java.util.List;

public interface OrderService {

    /**
     * 新增订单
     * @param orderCreateDTO 订单信息
     */
    void addOrder(OrderCreateDTO orderCreateDTO);

    /**
     * 删除订单
     * @param id 订单id
     */
    void removeOrder(Long id);

    /**
     * 更新订单
     * @param orderUpdateDTO 订单信息
     */
    void updateOrder(OrderUpdateDTO orderUpdateDTO);

    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单信息
     */
    Order queryOrderById(Long id);

    /**
     * 查询订单列表
     * @param orderQueryDTO 分页查询参数
     * @return 订单列表
     */
    PageInfo queryOrderList(OrderQueryDTO orderQueryDTO);

    /**
     * 查询某时间段内销量前10的商品
     * @param query 查询参数
     * @return 销量前10的商品列表
     */
    List<SalesTop10DTO> querySalesTop10(SalesTop10QueryDTO query);
}