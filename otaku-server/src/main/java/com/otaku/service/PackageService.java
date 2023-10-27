package com.otaku.service;

import com.otaku.dto.PackageDTO;
import com.otaku.dto.PackagePageQueryDTO;
import com.otaku.entity.Package;
import com.otaku.result.PageResult;
import com.otaku.vo.ProductItemVO;
import com.otaku.vo.PackageVO;

import java.util.List;

public interface PackageService {
    /**
     * 新增套餐 同时保存套餐和产品之间的绑定关系
     * @param packageDTO
     */
    void saveWithProduct(PackageDTO packageDTO);

    /**
     * 分页查询
     * @param packagePageQueryDTO
     * @return
     */
    PageResult pageQuery(PackagePageQueryDTO packagePageQueryDTO);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据ID查询套餐和绑定的产品数据
     * @param id
     * @return
     */
    PackageVO getByIdWithProduct(Long id);

    /**
     * 修改套餐
     * @param packageDTO
     */
    void update(PackageDTO packageDTO);

    /**
     * 套餐起售和停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 条件查询
     * @param aPackage
     * @return
     */
    List<Package> list(Package aPackage);

    /**
     * 根据id查询产品选项
     * @param id
     * @return
     */
    List<ProductItemVO> getProductItemById(Long id);
}
