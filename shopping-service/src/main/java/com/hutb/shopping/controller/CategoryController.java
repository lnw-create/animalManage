package com.hutb.shopping.controller;

import com.hutb.commonUtils.exception.CommonException;
import com.hutb.shopping.model.DTO.CategoryDTO;
import com.hutb.shopping.model.DTO.PageQueryListDTO;
import com.hutb.shopping.model.pojo.ResultInfo;
import com.hutb.shopping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("shopping/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     */
    @PostMapping("addCategory")
    public ResultInfo addCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            categoryService.addCategory(categoryDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 删除分类
     */
    @PostMapping("removeCategory")
    public ResultInfo removeCategory(@RequestParam Long id) {
        try {
            categoryService.removeCategory(id);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 更新分类
     */
    @PostMapping("editCategory")
    public ResultInfo updateCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            categoryService.updateCategory(categoryDTO);
            return ResultInfo.success();
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }

    /**
     * 查询分类列表
     */
    @GetMapping("allUser/queryCategoryList")
    public ResultInfo queryCategoryList(@RequestBody PageQueryListDTO pageQueryListDTO) {
        try {
            return ResultInfo.success(categoryService.queryCategoryList(pageQueryListDTO));
        } catch (CommonException e) {
            return ResultInfo.fail(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.fail("系统错误: " + e.getMessage());
        }
    }
}