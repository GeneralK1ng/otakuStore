package com.otaku.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface PackageProductMapper {
    /**
     * 根据产品ID查询对应的套餐ID
     * @param productIds
     * @return
     */
    List<Long> getPackageIdsByProductIds(List<Long> productIds);
}
