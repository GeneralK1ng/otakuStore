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
import java.util.Map;

@Mapper
public interface PackageMapper {

    /**
     * 根据分类 ID 查询套餐的数量。
     *
     * @param categoryId 分类的唯一标识 ID。
     * @return 返回符合给定分类 ID 的套餐数量。
     */
    @Select("select count(id) from package where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 向数据库中新增套餐。
     *
     * @param aPackage 要新增的套餐对象。
     */
    @AutoFill(OperationType.INSERT)
    void insert(Package aPackage);

    /**
     * 分页查询套餐。
     *
     * @param packagePageQueryDTO 包含分页查询条件的数据传输对象。
     * @return 返回包含套餐信息的分页结果对象。
     */
    Page<PackageVO> pageQuery(PackagePageQueryDTO packagePageQueryDTO);

    /**
     * 根据套餐 ID 查询套餐信息。
     *
     * @param id 套餐的唯一标识 ID。
     * @return 返回包含给定套餐 ID 的套餐对象，如果找不到则返回 null。
     */
    @Select("select * from package where id = #{id}")
    Package getById(Long id);

    /**
     * 根据套餐 ID 删除套餐。
     *
     * @param id 套餐的唯一标识 ID。
     */
    @Delete("delete from package where id = #{id}")
    void deleteById(Long id);

    /**
     * 修改套餐的基本信息。
     *
     * @param aPackage 包含要修改的套餐信息的对象。
     */
    @AutoFill(OperationType.UPDATE)
    void update(Package aPackage);

    /**
     * 根据动态条件查询套餐。
     *
     * @param aPackage 包含动态查询条件的套餐对象。
     * @return 返回符合给定条件的套餐对象的列表。
     */
    List<Package> list(Package aPackage);

    /**
     * 根据套餐 ID 查询包含产品选项的信息。
     *
     * @param packageId 套餐的唯一标识 ID。
     * @return 返回包含产品选项信息的列表，每个选项包括产品名称、份数、图片和描述。
     */
    @Select("select pp.name, pp.copies, p.image, p.description from " +
            "package_product pp left join product p on pp.package_id = p.id " +
            "where pp.package_id = #{packageId}")
    List<ProductItemVO> getProductItemByPackageId(Long packageId);

    /**
     * 根据动态条件查询数量。
     *
     * @param map 包含动态查询条件的参数映射。
     * @return 返回满足给定条件的数量。
     */
    Integer countByMap(Map<String, Object> map);
}
