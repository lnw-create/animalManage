package com.hutb.shopping.service;

import com.hutb.shopping.model.DTO.CategoryDTO;
import com.hutb.shopping.model.DTO.PageQueryListDTO;
import com.hutb.shopping.model.pojo.PageInfo;

public interface CategoryService {
    
    /**
     * 新增分类
     * @param categoryDTO 分类信息
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 删除分类
     * @param id 分类id
     */
    void removeCategory(Long id);

    /**
     * 更新分类
     * @param categoryDTO 分类信息
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     * 查询分类列表
     * @param pageQueryListDTO 分页查询参数
     * @return 分类列表
     */
    PageInfo queryCategoryList(PageQueryListDTO pageQueryListDTO);
}