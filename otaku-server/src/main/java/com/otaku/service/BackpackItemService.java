package com.otaku.service;

import com.otaku.dto.BagItemDTO;

public interface BackpackItemService {

    /**
     * 管理端新增背包物品
     * @param bagItemDTO 背包物品信息
     */
    void save(BagItemDTO bagItemDTO);
}
