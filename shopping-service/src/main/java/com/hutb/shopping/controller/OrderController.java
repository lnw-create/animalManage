package com.hutb.shopping.controller;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.shopping.model.DTO.order.OrderCreateDTO;
import com.hutb.shopping.model.DTO.order.OrderQueryDTO;
import com.hutb.shopping.model.DTO.order.OrderUpdateDTO;
import com.hutb.shopping.model.DTO.sales.SalesTop10DTO;
import com.hutb.shopping.model.DTO.sales.SalesTop10QueryDTO;
import com.hutb.shopping.model.pojo.Order;
import com.hutb.shopping.model.pojo.PageInfo;
import com.hutb.shopping.model.pojo.ResultInfo;
import com.hutb.shopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("shopping/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("normalVolunteer/createOrder")
    public ResultInfo createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        try {
            orderService.addOrder(orderCreateDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("normalVolunteer/getOrder")
    public ResultInfo getOrder(@RequestParam Long id) {
        try {
            Order order = orderService.queryOrderById(id);
            return ResultInfo.success(order);
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 更新订单
     */
    @PostMapping("normalVolunteer/updateOrder")
    public ResultInfo updateOrder(@RequestBody OrderUpdateDTO orderUpdateDTO) {
        try {
            orderService.updateOrder(orderUpdateDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 删除订单
     */
    @PostMapping("normalVolunteer/deleteOrder")
    public ResultInfo deleteOrder(@RequestParam Long id) {
        try {
            orderService.removeOrder(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 查询订单列表
     */
    @GetMapping("normalVolunteer/list")
    public ResultInfo getOrderList(OrderQueryDTO orderQueryDTO) {
        try {
            PageInfo pageInfo = orderService.queryOrderList(orderQueryDTO);
            return ResultInfo.success(pageInfo);
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 查询某时间段内销量前10的商品
     */
    @GetMapping("/sales/top10")
    public ResultInfo getSalesTop10(SalesTop10QueryDTO query) {
        try {
            List<SalesTop10DTO> top10Products = orderService.querySalesTop10(query);
            return ResultInfo.success(top10Products);
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
}