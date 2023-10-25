package com.otaku.service;

import com.otaku.dto.PackageDTO;
import com.otaku.dto.PackagePageQueryDTO;
import com.otaku.result.PageResult;

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
}
