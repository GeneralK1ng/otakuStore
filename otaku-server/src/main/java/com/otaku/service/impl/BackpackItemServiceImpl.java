package com.otaku.service.impl;

import com.otaku.dto.BagItemDTO;
import com.otaku.entity.BagItem;
import com.otaku.mapper.BackpackItemMapper;
import com.otaku.service.BackpackItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BackpackItemServiceImpl implements BackpackItemService {

    @Autowired
    private BackpackItemMapper backpackItemMapper;

    /**
     * 管理端新增背包物品（道具）
     * @param bagItemDTO 背包物品信息
     */
    @Override
    @Transactional
    public void save(BagItemDTO bagItemDTO) {
        BagItem bagItem = new BagItem();

        // 对象数据拷贝
        BeanUtils.copyProperties(bagItemDTO, bagItem);
        // 保存
        backpackItemMapper.insert(bagItem);

    }
}
