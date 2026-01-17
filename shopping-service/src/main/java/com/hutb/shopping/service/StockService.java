package com.hutb.shopping.service;

import com.hutb.shopping.model.DTO.StockDTO;
import com.hutb.shopping.model.pojo.PageInfo;
import com.hutb.shopping.model.DTO.PageQueryListDTO;

public interface StockService {
    
    /**
     * 新增库存
     * @param stockDTO 库存信息
     */
    void addStock(StockDTO stockDTO);

    /**
     * 删除库存
     * @param id 库存id
     */
    void removeStock(Long id);

    /**
     * 更新库存
     * @param stockDTO 库存信息
     */
    void updateStock(StockDTO stockDTO);

    /**
     * 查询库存列表
     * @param pageQueryListDTO 分页查询参数
     * @return 库存列表
     */
    PageInfo queryStockList(PageQueryListDTO pageQueryListDTO);
}
