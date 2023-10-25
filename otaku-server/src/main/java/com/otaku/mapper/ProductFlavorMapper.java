package com.otaku.mapper;

import com.otaku.entity.ProductFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductFlavorMapper {
    /**
     * 批量插入偏好数据
     * @param flavors
     */
    void insertBatch(List<ProductFlavor> flavors);

    /**
     * 根据产品ID删除偏好数据
     * @param productId
     */
    @Delete("delete from product_flavor where product_id = #{productId}")
    void deleteByProductId(Long productId);

    /**
     * 根据产品ID集合批量删除关联的偏好数据
     * @param productIds
     */
    void deleteByProductIds(List<Long> productIds);

    /**
     *根据产品ID查询对应的偏好数据
     * @param productId
     * @return
     */
    @Select("select * from product_flavor where product_id = #{productId}")
    List<ProductFlavor> getByProductId(Long productId);
}
