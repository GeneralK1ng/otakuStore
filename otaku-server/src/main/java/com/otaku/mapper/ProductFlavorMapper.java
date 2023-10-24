package com.otaku.mapper;

import com.otaku.entity.ProductFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductFlavorMapper {
    /**
     * 批量插入偏好数据
     * @param flavors
     */
    void insertBatch(List<ProductFlavor> flavors);
}
