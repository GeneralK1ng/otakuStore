package com.otaku.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserPointRewardDTO implements Serializable {

    // 用户ID
    private Long userId;

    // 积分
    private Integer quantity;

    // 物品类型
    public static final Long itemId = 1L;

}
