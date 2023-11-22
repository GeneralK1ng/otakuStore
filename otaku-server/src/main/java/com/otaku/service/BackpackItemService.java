package com.otaku.service;

import com.otaku.dto.BagItemDTO;
import com.otaku.dto.BagItemPageQueryDTO;
import com.otaku.result.PageResult;

import java.util.List;

public interface BackpackItemService {

    /**
     * 管理端新增背包物品
     * @param bagItemDTO 背包物品信息
     */
    void save(BagItemDTO bagItemDTO);

    /**
     * 管理端批量删除背包物品
     * @param ids 物品id列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 管理端查询背包物品分页
     * @param bagItemPageQueryDTO 背包物品分页查询条件
     * @return 背包物品分页结果
     */
    PageResult pageQuery(BagItemPageQueryDTO bagItemPageQueryDTO);
}
