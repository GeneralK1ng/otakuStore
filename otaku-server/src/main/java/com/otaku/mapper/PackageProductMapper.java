package com.otaku.mapper;

import com.otaku.entity.PackageProduct;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface PackageProductMapper {
    /**
     * 根据产品ID查询对应的套餐ID
     * @param productIds
     * @return
     */
    List<Long> getPackageIdsByProductIds(List<Long> productIds);

    /**
     * 批量保存套餐和产品的绑定关系
     * @param packageProducts
     */
    void insertBatch(List<PackageProduct> packageProducts);

    /**
     * 根据套餐ID删除套餐和产品的绑定关系
     * @param packageId
     */
    @Delete("delete from package_product where package_id = #{packageId}")
    void deleteByPackageId(Long packageId);

    /**
     * 根据套餐ID查询套餐和产品的绑定关系
     * @param packageId
     * @return
     */
    @Select("select * from package_product where package_id = #{packageId}")
    List<PackageProduct> getByPackageId(Long packageId);
}
