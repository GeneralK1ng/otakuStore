package com.otaku.mapper;

import com.otaku.dto.UserPointRewardDTO;
import com.otaku.entity.UserBackpack;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BackpackMapper {

    /**
     * 奖励积分
     *
     * @param userBackpack 用户背包数据
     */
    @Insert("insert into user_backpack" +
            "        (user_id, item_id, quantity, status, idempotent)" +
            "        values (#{u.userId}, #{u.itemId}, #{u.quantity}, #{u.status}, #{u.idempotent})")
    void insertPoint(@Param("u") UserBackpack userBackpack);
}
