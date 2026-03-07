package com.hutb.shopping.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.UserContext;
import com.hutb.shopping.client.VolunteerServiceClient;
import com.hutb.shopping.constant.ShoppingConstant;
import com.hutb.shopping.mapper.OrderMapper;
import com.hutb.shopping.model.DTO.order.OrderCreateDTO;
import com.hutb.shopping.model.DTO.order.OrderQueryDTO;
import com.hutb.shopping.model.DTO.order.OrderUpdateDTO;
import com.hutb.shopping.model.DTO.sales.SalesTop10DTO;
import com.hutb.shopping.model.DTO.sales.SalesTop10QueryDTO;
import com.hutb.shopping.model.pojo.Order;
import com.hutb.shopping.model.pojo.PageInfo;
import com.hutb.shopping.model.pojo.ResultInfo;
import com.hutb.shopping.service.OrderService;
import com.hutb.shopping.utils.CommonValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private VolunteerServiceClient volunteerServiceClient;

    /**
     * 新增订单
     * @param orderCreateDTO 订单信息
     */
    @Override
    @Transactional
    public void addOrder(OrderCreateDTO orderCreateDTO) {
        log.info("添加订单：{}", orderCreateDTO);
    
        // 1. 参数校验
        CommonValidate.validateOrder(orderCreateDTO);
    
        // 2. 获取用户 ID（如果未传入）
        Long userId = orderCreateDTO.getUserId();
        if (userId == null) {
            userId = UserContext.getUserId();
            if (userId == null) {
                throw new CommonException("用户未登录");
            }
            orderCreateDTO.setUserId(userId);
        }
    
        // 3. 查询用户积分余额并校验
        Integer userPoints = getUserPointBalance(userId);
        if (userPoints == null || userPoints < 0) {
            throw new CommonException("用户积分查询失败");
        }

        Integer orderTotalPoints = orderCreateDTO.getPrice();
        if (orderTotalPoints == null || orderTotalPoints <= 0) {
            throw new CommonException("订单积分必须大于 0");
        }

        if (userPoints < orderTotalPoints) {
            throw new CommonException("积分不足，当前积分：" + userPoints + ", 需要积分：" + orderTotalPoints);
        }
    
        // 4. 设置订单默认值
        Order order = new Order();
        BeanUtils.copyProperties(orderCreateDTO, order);
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setProductId(orderCreateDTO.getProductId());
        order.setProductName(orderCreateDTO.getProductName());
        order.setTotalIntegral(orderTotalPoints);
        order.setStatus(orderCreateDTO.getStatus());
        order.setShippingAddress(orderCreateDTO.getShippingAddress());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setCreateUser(UserContext.getUsername());
        order.setUpdateUser(UserContext.getUsername());
    
        // 5. 新增订单
        int result = orderMapper.addOrder(order);
        if (result == 0) {
            throw new CommonException("添加订单信息失败");
        }

        //todo 扣减库存
            
        // 6. 扣减用户积分
        try {
            deductUserPoints(userId, orderTotalPoints);
            log.info("扣减积分成功：userId={}, 扣减积分={}", userId, orderTotalPoints);
        } catch (Exception e) {
            log.error("扣减积分失败：userId={}, 扣减积分={}", userId, orderTotalPoints, e);
            // 积分扣减失败，抛出异常回滚订单
            throw new CommonException("积分扣减失败，订单创建失败：" + e.getMessage());
        }
            
        log.info("添加订单成功，订单号：{}, 消耗积分：{}", order.getOrderNumber(), orderTotalPoints);
    }
    
    /**
     * 查询用户积分余额
     * @param userId 用户 ID
     * @return 积分余额
     */
    private Integer getUserPointBalance(Long userId) {
        ResultInfo<Integer> result = volunteerServiceClient.getPointBalance(userId);
        
        // 检查远程调用结果
        if (result == null || !"1".equals(result.getCode())) {
            log.error("查询积分余额失败：userId={}, message={}", userId, 
                result != null ? result.getMsg() : "unknown error");
            throw new CommonException("查询积分余额失败：" + 
                (result != null ? result.getMsg() : "unknown error"));
        }
        
        Integer balance = result.getData();
        if (balance == null) {
            balance = 0;
        }
        
        log.info("查询积分余额成功：userId={}, balance={}", userId, balance);
        return balance;
    }
    
    /**
     * 扣减用户积分
     * @param userId 用户 ID
     * @param points 积分数量
     */
    private void deductUserPoints(Long userId, Integer points) {
        ResultInfo<Void> result = volunteerServiceClient.deductPoints(userId, points);
        
        // 检查远程调用结果
        if (result == null || !"1".equals(result.getCode())) {
            log.error("扣减积分失败：userId={}, points={}, message={}", userId, points,
                result != null ? result.getMsg() : "unknown error");
            throw new CommonException("扣减积分失败：" + 
                (result != null ? result.getMsg() : "unknown error"));
        }
        
        log.info("扣减积分成功：userId={}, points={}", userId, points);
    }

    /**
     * 删除订单
     * @param id 订单id
     */
    @Override
    @Transactional
    public void removeOrder(Long id) {
        log.info("删除订单信息:id-{}", id);

        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new CommonException("删除订单id不能为空");
        }

        // 2. 判断订单是否存在
        Order order = orderMapper.queryOrderById(id);
        if (order == null) {
            throw new CommonException("订单信息不存在");
        }

        // 3. 删除订单（软删除）
        long removed = orderMapper.removeOrder(id, ShoppingConstant.ORDER_STATUS_DELETED, UserContext.getUsername());
        if (removed == 0) {
            throw new CommonException("删除订单信息失败");
        }
        log.info("删除订单信息成功");
    }

    /**
     * 更新订单
     * @param orderUpdateDTO 订单信息
     */
    @Override
    public void updateOrder(OrderUpdateDTO orderUpdateDTO) {
        log.info("更新订单信息: {}", orderUpdateDTO);

        // 1. 参数校验
        Long id = orderUpdateDTO.getId();
        if (id == null || id <= 0) {
            throw new CommonException("更新订单id不能为空");
        }

        Order order = orderMapper.queryOrderById(id);
        if (order == null) {
            throw new CommonException("订单信息不存在");
        }

        // 2. 更新订单
        order.setId(id);
        if (orderUpdateDTO.getStatus() != null) {
            order.setStatus(orderUpdateDTO.getStatus());
        }
        if (orderUpdateDTO.getShippingAddress() != null) {
            order.setShippingAddress(orderUpdateDTO.getShippingAddress());
        }
        order.setUpdateTime(new Date());
        order.setUpdateUser(UserContext.getUsername());
        
        long updated = orderMapper.updateOrder(order);
        if (updated == 0) {
            throw new CommonException("更新订单信息失败");
        }
        log.info("更新订单信息成功");
    }

    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单信息
     */
    @Override
    public Order queryOrderById(Long id) {
        log.info("查询订单信息:id-{}", id);

        if (id == null || id <= 0) {
            throw new CommonException("查询订单id不能为空");
        }

        Order order = orderMapper.queryOrderById(id);
        if (order == null) {
            throw new CommonException("订单信息不存在");
        }

        log.info("查询订单信息成功");
        return order;
    }

    /**
     * 查询订单列表
     * @param orderQueryDTO 分页查询参数
     * @return 订单列表
     */
    @Override
    public PageInfo queryOrderList(OrderQueryDTO orderQueryDTO) {
        log.info("查询订单列表: {}", orderQueryDTO);

        // 分页查询
        Page<Object> page = PageHelper.startPage(orderQueryDTO.getPageNum(), orderQueryDTO.getPageSize());
        List<Order> orders = orderMapper.queryOrderList(orderQueryDTO);
        com.github.pagehelper.PageInfo<Order> pageInfo = new com.github.pagehelper.PageInfo<>(orders);
        log.info("查询订单列表成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 查询某时间段内销量前10的商品
     * @param query 查询参数
     * @return 销量前10的商品列表
     */
    @Override
    public List<SalesTop10DTO> querySalesTop10(SalesTop10QueryDTO query) {
        log.info("查询销量Top10商品: {}", query);
        List<SalesTop10DTO> top10Products = orderMapper.querySalesTop10(query);
        log.info("查询销量Top10商品成功，共{}条记录", top10Products.size());
        return top10Products;
    }

    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNumber() {
        // 使用UUID生成唯一订单号
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "ORD" + System.currentTimeMillis() + uuid.substring(0, 8).toUpperCase();
    }
}