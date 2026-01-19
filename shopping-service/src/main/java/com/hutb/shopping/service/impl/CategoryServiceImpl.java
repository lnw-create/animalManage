package com.hutb.shopping.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.UserContext;
import com.hutb.shopping.constant.ShoppingConstant;
import com.hutb.shopping.model.pojo.PageInfo;
import com.hutb.shopping.model.DTO.PageQueryListDTO;
import com.hutb.shopping.model.pojo.Category;
import com.hutb.shopping.utils.CommonValidate;
import com.hutb.shopping.mapper.CategoryMapper;
import com.hutb.shopping.model.DTO.CategoryDTO;
import com.hutb.shopping.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增分类
     * @param categoryDTO 分类信息
     */
    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        log.info("添加分类: {}", categoryDTO);

        // 1. 参数校验
        CommonValidate.validateCategory(categoryDTO);

        // 2. 判断分类是否已存在
        Category category = categoryMapper.queryCategoryByName(categoryDTO.getName(), ShoppingConstant.CATEGORY_STATUS_DELETED);
        if (category != null){
            throw new CommonException("分类已存在");
        }

        // 3. 设置默认值
        categoryDTO.setStatus(ShoppingConstant.CATEGORY_STATUS_NORMAL); // 默认状态为正常
        categoryDTO.setCreateTime(new Date());
        categoryDTO.setUpdateTime(new Date());
        categoryDTO.setCreateUser(UserContext.getUsername());
        categoryDTO.setUpdateUser(UserContext.getUsername());

        // 4. 新增
        int i = categoryMapper.addCategory(categoryDTO);
        if (i == 0) {
            throw new CommonException("添加分类信息失败");
        }
        log.info("添加分类成功");
    }

    /**
     * 删除分类
     * @param id 分类id
     */
    @Override
    public void removeCategory(Long id) {
        log.info("删除商品分类信息:id-{}", id);

        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new CommonException("删除分类id不能为空");
        }

        // 2. 判断分类是否存在
        Category category = categoryMapper.queryCategoryById(id, ShoppingConstant.CATEGORY_STATUS_DELETED);
        if (category == null) {
            throw new CommonException("分类信息不存在");
        }

        // 3. 删除
        long removed = categoryMapper.removeCategory(id, ShoppingConstant.CATEGORY_STATUS_DELETED, UserContext.getUsername());
        if (removed == 0) {
            throw new CommonException("删除分类信息失败");
        }
        log.info("删除分类信息成功");
    }

    /**
     * 更新分类
     * @param categoryDTO 分类信息
     */
    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        log.info("更新分类信息: {}", categoryDTO);

        // 1. 参数校验
        Long id = categoryDTO.getId();
        if (id == null || id <= 0) {
            throw new CommonException("更新分类id不能为空");
        }
        CommonValidate.validateCategory(categoryDTO);

        Category category = categoryMapper.queryCategoryById(id, ShoppingConstant.CATEGORY_STATUS_DELETED);
        if (category == null) {
            throw new CommonException("分类信息不存在");
        }

        // 3. 更新分类
        categoryDTO.setUpdateTime(new Date());
        categoryDTO.setUpdateUser(UserContext.getUsername());
        long updated = categoryMapper.updateCategory(categoryDTO);
        if (updated == 0) {
            throw new CommonException("更新分类信息失败");
        }
        log.info("更新分类信息成功");
    }

    /**
     * 查询分类列表
     * @param pageQueryListDTO 分页查询参数
     * @return 分类列表
     */
    @Override
    public PageInfo queryCategoryList(PageQueryListDTO pageQueryListDTO) {
        log.info("查询分类列表: {}", pageQueryListDTO);

        // 分页查询
        Page<Object> page = PageHelper.startPage(pageQueryListDTO.getPageNum(), pageQueryListDTO.getPageSize());
        List<Category> categorys = categoryMapper.queryCategoryListWithPage(pageQueryListDTO);
        com.github.pagehelper.PageInfo<Category> pageInfo = new com.github.pagehelper.PageInfo<>(categorys);
        log.info("查询分类列表成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }
}