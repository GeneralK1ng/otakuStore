package com.otaku.mapper;

import com.github.pagehelper.Page;
import com.otaku.annotation.AutoFill;
import com.otaku.dto.PackagePageQueryDTO;
import com.otaku.entity.Package;
import com.otaku.enumeration.OperationType;
import com.otaku.vo.PackageVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
