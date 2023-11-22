package com.otaku.dto;

import lombok.Data;

import java.io.Serializable;

@Data

public class BagItemDTO implements Serializable {

    // 物品图片路径
    private String image;

    // 物品名称
    private String name;

    // 物品描述
    private String description;
}
