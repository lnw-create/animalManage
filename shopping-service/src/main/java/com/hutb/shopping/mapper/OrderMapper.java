package com.hutb.shopping.mapper;

import com.hutb.shopping.model.DTO.sales.SalesTop10DTO;
import com.hutb.shopping.model.DTO.sales.SalesTop10QueryDTO;
import com.hutb.shopping.model.DTO.order.OrderQueryDTO;
import com.hutb.shopping.model.pojo.Order;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderMapper {

    /**
     * 新增订单
     * @param order 订单信息
     * @return 影响行数
     */
    @Insert("INSERT INTO orders (user_id, order_number, total_integral, status, create_time, update_time, create_user, update_user, shipping_address, product_id, product_name, price) " +
            "VALUES (#{userId}, #{orderNumber}, #{totalIntegral}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{shippingAddress}, #{productId}, #{productName}, #{price})")
    int addOrder(Order order);

    /**
     * 根据ID删除订单（软删除，更新状态为deleted）
     * @param id 订单ID
     * @param status 状态值
     * @param updateUser 修改人
     * @return 影响行数
     */
    @Update("UPDATE orders SET status = #{status}, update_time = NOW(), update_user = #{updateUser} WHERE id = #{id}")
    int removeOrder(long id, String status, String updateUser);

    /**
     * 更新订单
     * @param order 订单信息
     * @return 影响行数
     */
    @Update("UPDATE orders SET status = #{status}, shipping_address = #{shippingAddress}, update_time = #{updateTime}, update_user = #{updateUser} " +
            "WHERE id = #{id}")
    int updateOrder(Order order);

    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单信息
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order queryOrderById(long id);

    /**
     * 查询订单列表（分页）
     * @param orderQueryDTO 查询参数
     * @return 订单列表
     */
    List<Order> queryOrderList(@Param("query") OrderQueryDTO orderQueryDTO);

    /**
     * 查询某时间段内销量前10的商品
     * @param query 查询参数
     * @return 销量前10的商品列表
     */
    List<SalesTop10DTO> querySalesTop10(SalesTop10QueryDTO query);
}