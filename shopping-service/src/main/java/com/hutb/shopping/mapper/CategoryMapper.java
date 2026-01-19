package com.hutb.shopping.mapper;

import com.hutb.shopping.model.DTO.CategoryDTO;
import com.hutb.shopping.model.DTO.PageQueryListDTO;
import com.hutb.shopping.model.pojo.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CategoryMapper {
    
    /**
     * 新增分类
     * @param categoryDTO 分类信息
     * @return 影响行数
     */
    @Insert("insert into category (name, description, sort, create_time, update_time, status, create_user, update_user) " +
            "VALUES (#{name}, #{description}, #{sort}, #{createTime}, #{updateTime}, #{status}, #{createUser}, #{updateUser})")
    int addCategory(CategoryDTO categoryDTO);

    /**
     * 根据ID删除分类（软删除，更新状态为-1）
     * @param id 分类ID
     * @param status 状态值
     * @param updateUser 修改人
     * @return 影响行数
     */
    @Update("UPDATE category SET status = #{status}, update_time = now(), update_user = #{updateUser} WHERE id = #{id}")
    int removeCategory(long id, String status, String updateUser);

    /**
     * 更新分类
     * @param categoryDTO 分类信息
     * @return 影响行数
     */
    @Update("UPDATE category SET name = #{name}, description = #{description}, sort = #{sort}, " +
            "update_time = now(), update_user = #{updateUser} WHERE id = #{id}")
    int updateCategory(CategoryDTO categoryDTO);

    /**
     * 根据分类名称查询分类
     * @param name 分类名称
     * @param status 排除的状态值
     * @return 分类信息
     */
    @Select("select * from category where name = #{name} and status != #{status}")
    Category queryCategoryByName(String name, String status);

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类信息
     */
    @Select("select * from category where id = #{id} and status != #{status}")
    Category queryCategoryById(long id, String status);

    /**
     * 查询分类列表（分页）
     * @param pageQueryListDTO 分页查询参数
     * @return 分类列表
     */
    List<Category> queryCategoryListWithPage(PageQueryListDTO pageQueryListDTO);
}