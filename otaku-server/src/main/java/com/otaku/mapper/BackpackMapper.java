package com.otaku.mapper;

import com.otaku.entity.UserBackpack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BackpackMapper {

    /**
     * 奖励积分
     *
     * @param userBackpack 用户背包数据
     */
    void insertPoint(@Param("u") UserBackpack userBackpack);

    /**
     * 根据物品id删除
     *
     * @param itemIds 物品id
     */
    void deleteBatchByItemIds(List<Long> itemIds);

    /**
     * 根据物品id查询用户id
     *
     * @param id 物品id
     * @return 用户id
     */
    @Select("select user_id from user_backpack where item_id = #{id}")
    List<Long> getUserIdsByItemId(Long id);

    /**
     * 根据物品id查询用户数量
     *
     * @param ids 物品id
     * @return 用户id
     */
    @Select("select count(1) from user_backpack where item_id in (#{ids})")
    int countByItemIds(List<Long> ids);
}
