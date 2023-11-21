package com.otaku.mapper;

import com.otaku.dto.UserPointRewardDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserBackpackMapper {

    /**
     * 更新用户积分
     *
     * @param userPointRewardDTO 积分信息
     */
    void updatePoint(@Param("userPointRewardDTO") UserPointRewardDTO userPointRewardDTO);
}
