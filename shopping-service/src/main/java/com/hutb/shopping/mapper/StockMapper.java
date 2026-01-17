package com.hutb.shopping.mapper;

import com.hutb.shopping.model.DTO.StockDTO;
import com.hutb.shopping.model.DTO.PageQueryListDTO;
import com.hutb.shopping.model.pojo.Stock;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StockMapper {

    /**
     * 新增库存
     * @param stockDTO 库存信息
     * @return 影响行数
     */
    @Insert("insert into stock (product_id, product_name, quantity, create_time, update_time, status , create_user , update_user ) " +
            "VALUES (#{productId}, #{productName}, #{quantity}, #{createTime}, #{updateTime}, #{status}, #{createUser}, #{updateUser})")
    int addStock(StockDTO stockDTO);

    /**
     * 根据ID删除库存（软删除，更新状态为-1）
     * @param id 库存ID
     * @param status 状态值
     * @param updateUser 修改人
     * @return 影响行数
     */
    @Update("UPDATE stock SET status = #{status}, update_time = now(), update_user = #{updateUser} WHERE id = #{id}")
    int removeStock(long id, String status, String updateUser);

    /**
     * 更新库存
     * @param stockDTO 库存信息
     * @return 影响行数
     */
    @Update("UPDATE stock SET product_name = #{productName}, quantity = #{quantity}, " +
            "update_time = now() WHERE id = #{id}")
    int updateStock(StockDTO stockDTO);

    /**
     * 根据ID查询库存
     * @param id 库存ID
     * @param status 排除的状态值
     * @return 库存信息
     */
    @Select("select * from stock where id = #{id} and status != #{status}")
    Stock queryStockById(long id, String status);

    /**
     * 查询库存列表（分页）
     * @param pageQueryListDTO 分页查询参数
     * @return 库存列表
     */
    List<Stock> queryStockList(PageQueryListDTO pageQueryListDTO);
}