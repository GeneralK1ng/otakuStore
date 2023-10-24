package com.otaku.mapper;

import com.otaku.entity.ProductFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

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
}
