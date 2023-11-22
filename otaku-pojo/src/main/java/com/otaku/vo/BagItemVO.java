package com.otaku.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BagItemVO implements Serializable {

    // 物品id
    private Long id;

    // 物品图片路径
    private String image;

    // 物品名称
    private String name;

    // 物品描述
    private String description;

    // 更新时间
    private LocalDateTime updateTime;

    // 持有这些物品的用户
    private List<UserVO> users;

    // 持有这些物品的用户数量
    private Integer userCount;
}
