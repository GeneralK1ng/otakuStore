package com.otaku.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.otaku.dto.BagItemDTO;
import com.otaku.dto.BagItemPageQueryDTO;
import com.otaku.entity.BagItem;
import com.otaku.mapper.BackpackItemMapper;
import com.otaku.mapper.BackpackMapper;
import com.otaku.mapper.UserMapper;
import com.otaku.result.PageResult;
import com.otaku.service.BackpackItemService;
import com.otaku.vo.BagItemVO;
import com.otaku.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class BackpackItemServiceImpl implements BackpackItemService {

    @Autowired
    private BackpackItemMapper backpackItemMapper;

    @Autowired
    private BackpackMapper backpackMapper;

    @Autowired
    private UserMapper userMapper;

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

    /**
     * 管理端删除背包物品（道具）
     * @param ids 背包物品id
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        if (!ids.isEmpty()) {
            // 批量删除背包物品
            backpackItemMapper.deleteBatch(ids);

            // 判断背包物品是否关联了用户
            if (backpackMapper.countByItemIds(ids) > 0) {
                // 批量删除背包物品
                backpackMapper.deleteBatchByItemIds(ids);
            }
        }
    }

    /**
     * 管理端分页查询背包物品（道具）
     * @param bagItemPageQueryDTO 背包物品分页查询条件
     * @return 背包物品分页查询结果
     */
    @Override
    public PageResult pageQuery(BagItemPageQueryDTO bagItemPageQueryDTO) {
        // 开始分页
        PageHelper.startPage(bagItemPageQueryDTO.getPage(), bagItemPageQueryDTO.getPageSize());
        // 获取page对象
        Page<BagItemVO> page = backpackItemMapper.pageQuery(bagItemPageQueryDTO);

        // 遍历结果列表，为每个 BagItemVO 设置 users 属性
        for (BagItemVO bagItem : page.getResult()) {
            // 根据物品ID（假设是 itemId 属性）从 UserMapper 中获取用户列表
            List<Long> userIds = backpackMapper.getUserIdsByItemId(bagItem.getId());
            if (userIds != null) {
                List<UserVO> users = userMapper.listByIds(userIds);
                bagItem.setUserCount(users.size());
                bagItem.setUsers(users);
            }
        }

        return new PageResult(page.getTotal(), page.getResult());
    }
}
