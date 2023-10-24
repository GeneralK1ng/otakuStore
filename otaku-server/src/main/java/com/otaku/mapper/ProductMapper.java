package com.otaku.mapper;

import com.github.pagehelper.Page;
import com.otaku.annotation.AutoFill;
import com.otaku.dto.ProductPageQueryDTO;
import com.otaku.entity.Product;
import com.otaku.enumeration.OperationType;
import com.otaku.vo.ProductVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper {

    /**
     * 根据分类id查询产品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from product where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入产品数据
     * @param product
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Product product);

    /**
     * 产品分页查询
     * @param productPageQueryDTO
     * @return
     */
    Page<ProductVO> pageQuery(ProductPageQueryDTO productPageQueryDTO);

    /**
     * 根据主键查询产品
     * @param id
     * @return
     */
    @Select("select * from product where id = #{id}")
    Product getById(Long id);

    /**
     * 根据主键删除产品
     * @param id
     */
    @Delete("delete from product where id = #{id}")
    void deleteById(Long id);
}
