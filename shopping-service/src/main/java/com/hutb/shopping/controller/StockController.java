package com.hutb.shopping.controller;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.shopping.model.DTO.StockDTO;
import com.hutb.shopping.model.DTO.PageQueryListDTO;
import com.hutb.shopping.model.pojo.ResultInfo;
import com.hutb.shopping.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("shopping/stock")
public class StockController {
    
    @Autowired
    private StockService stockService;

    /**
     * 新增库存
     */
    @PostMapping("addStock")
    public ResultInfo addStock(@RequestBody StockDTO stockDTO) {
        try {
            stockService.addStock(stockDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 删除库存
     */
    @PostMapping("removeStock")
    public ResultInfo removeStock(@RequestParam Long id) {
        try {
            stockService.removeStock(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 更新库存
     */
    @PostMapping("editStock")
    public ResultInfo updateStock(@RequestBody StockDTO stockDTO) {
        try {
            stockService.updateStock(stockDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 查询库存列表
     */
    @GetMapping("allUser/queryStockList")
    public ResultInfo queryStockList(@RequestBody PageQueryListDTO pageQueryListDTO) {
        try {
            return ResultInfo.success(stockService.queryStockList(pageQueryListDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
}
