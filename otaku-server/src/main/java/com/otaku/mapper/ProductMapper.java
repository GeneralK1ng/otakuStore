package com.otaku.mapper;

import com.otaku.annotation.AutoFill;
import com.otaku.entity.Product;
import com.otaku.enumeration.OperationType;
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
}
