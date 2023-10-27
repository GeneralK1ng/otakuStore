package com.otaku.mapper;

import com.github.pagehelper.Page;
import com.otaku.annotation.AutoFill;
import com.otaku.dto.PackagePageQueryDTO;
import com.otaku.entity.Package;
import com.otaku.enumeration.OperationType;
import com.otaku.vo.PackageVO;
import com.otaku.vo.ProductItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PackageMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from package where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐
     * @param aPackage
     */
    @AutoFill(OperationType.INSERT)
    void insert(Package aPackage);

    /**
     * 分页查询
     * @param packagePageQueryDTO
     * @return
     */
    Page<PackageVO> pageQuery(PackagePageQueryDTO packagePageQueryDTO);

    /**
     * 根据ID查询套餐
     * @param id
     * @return
     */
    @Select("select * from package where id = #{id}")
    Package getById(Long id);

    /**
     * 根据ID删除套餐
     * @param packageId
     */
    @Delete("delete from package where id = #{id}")
    void deleteById(Long packageId);

    /**
     * 修改套餐基本信息
     * @param aPackage
     */
    void update(Package aPackage);

    /**
     * 动态条件查询套餐
     * @param aPackage
     * @return
     */
    List<Package> list(Package aPackage);

    /**
     * 根据套餐id查询产品选项
     * @param packageId
     * @return
     */
    @Select("select pp.name, pp.copies, p.image, p.description from " +
            "package_product pp left join product p on pp.package_id = p.id " +
            "where pp.package_id = #{packageId}")
    List<ProductItemVO> getProductItemByPackageId(Long packageId);
}
