package com.hutb.shopping.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.UserContext;
import com.hutb.shopping.constant.ShoppingConstant;
import com.hutb.shopping.mapper.CategoryMapper;
import com.hutb.shopping.model.pojo.Category;
import com.hutb.shopping.model.pojo.PageInfo;
import com.hutb.shopping.model.DTO.PageQueryListDTO;
import com.hutb.shopping.model.pojo.Stock;
import com.hutb.shopping.utils.CommonValidate;
import com.hutb.shopping.mapper.StockMapper;
import com.hutb.shopping.model.DTO.StockDTO;
import com.hutb.shopping.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增库存
     * @param stockDTO 库存信息
     */
    @Override
    public void addStock(StockDTO stockDTO) {
        log.info("添加库存: {}", stockDTO);

        // 1. 参数校验
        CommonValidate.validateStock(stockDTO);

        // 2. 判断库存是否已存在
        Stock stock = stockMapper.queryStockByProductName(stockDTO.getProductName(), ShoppingConstant.STOCK_STATUS_DELETED);
        if (stock != null){
            throw new CommonException("库存已存在");
        }

        if (stockDTO.getCategoryId() == null || stockDTO.getCategoryId() <= 0) {
            //4.查询商品分类信息是否存在
            Category category = categoryMapper.queryCategoryById(stockDTO.getCategoryId(), ShoppingConstant.CATEGORY_STATUS_DELETED);
            if (category == null){
                throw new CommonException("商品分类信息不存在");
            }
        }

        // 3. 设置默认值
        stockDTO.setStatus(ShoppingConstant.STOCK_STATUS_NORMAL); // 默认状态为正常
        stockDTO.setCreateTime(new Date());
        stockDTO.setUpdateTime(new Date());
        stockDTO.setCreateUser(UserContext.getUsername());
        stockDTO.setUpdateUser(UserContext.getUsername());

        // 4. 新增
        int i = stockMapper.addStock(stockDTO);
        if (i == 0) {
            throw new CommonException("添加库存信息失败");
        }
        log.info("添加库存成功");
    }

    /**
     * 删除库存
     * @param id 库存id
     */
    @Override
    public void removeStock(Long id) {
        log.info("删除商品库存信息:id-{}", id);

        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new CommonException("删除商品id不能为空");
        }

        // 2. 判断库存是否存在
        Stock stock = stockMapper.queryStockById(id, ShoppingConstant.STOCK_STATUS_DELETED);
        if (stock == null) {
            throw new CommonException("商品信息不存在");
        }

        // 3. 删除
        long removed = stockMapper.removeStock(id, ShoppingConstant.STOCK_STATUS_DELETED,UserContext.getUsername());
        if (removed == 0) {
            throw new CommonException("删除商品库存信息失败");
        }
        log.info("删除商品库存信息成功");
    }

    /**
     * 更新库存
     * @param stockDTO 库存信息
     */
    @Override
    public void updateStock(StockDTO stockDTO) {
        log.info("更新库存信息: {}", stockDTO);

        // 1. 参数校验
        Long id = stockDTO.getId();
        if (id == null || id <= 0) {
            throw new CommonException("更新库存id不能为空");
        }
        CommonValidate.validateStock(stockDTO);

        Stock stock = stockMapper.queryStockById(id, ShoppingConstant.STOCK_STATUS_DELETED);
        if (stock == null) {
            throw new CommonException("商品信息不存在");
        }

        // 2. 判断是否修改关联分类信息
        if (stockDTO.getCategoryId() != null){
            if (!stock.getCategoryId().equals(stockDTO.getCategoryId())){
                //查询商品分类信息是否存在
                Category category = categoryMapper.queryCategoryById(stockDTO.getCategoryId(), ShoppingConstant.CATEGORY_STATUS_DELETED);
                if (category == null){
                    throw new CommonException("商品分类信息不存在");
                }
            }
        }

        // 4. 更新库存
        stockDTO.setUpdateTime(new Date());
        stockDTO.setUpdateUser(UserContext.getUsername());
        long updated = stockMapper.updateStock(stockDTO);
        if (updated == 0) {
            throw new CommonException("更新库存信息失败");
        }
        log.info("更新库存信息成功");
    }

    /**
     * 查询库存列表
     * @param pageQueryListDTO 分页查询参数
     * @return 库存列表
     */
    @Override
    public PageInfo queryStockList(PageQueryListDTO pageQueryListDTO) {
        log.info("查询库存列表: {}", pageQueryListDTO);

        // 分页查询
        Page<Object> page = PageHelper.startPage(pageQueryListDTO.getPageNum(), pageQueryListDTO.getPageSize());
        List<com.hutb.shopping.model.pojo.Stock> stocks = stockMapper.queryStockList(pageQueryListDTO);
        com.github.pagehelper.PageInfo<Stock> pageInfo = new com.github.pagehelper.PageInfo<>(stocks);
        log.info("查询库存列表成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }
}
