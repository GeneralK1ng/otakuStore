package com.otaku.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemVO implements Serializable {

    //产品名称
    private String name;

    //份数
    private Integer copies;

    //产品图片
    private String image;

    //产品描述
    private String description;
}
