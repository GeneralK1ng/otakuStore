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
public class BagItem implements Serializable {

    // 物品id
    private Long id;

    // 物品图片路径
    private String image;

    // 物品名称
    private String name;

    // 物品描述
    private String description;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}
