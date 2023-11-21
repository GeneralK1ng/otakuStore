package com.otaku.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBackpack implements Serializable {

    // 主键
    private Long id;

    // 用户ID
    private Long userId;

    // 物品ID
    private Long itemId;

    // 物品状态
    private Integer status;

    // 物品数量
    private Integer quantity;

    // 幂等ID
    private String idempotent;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}
