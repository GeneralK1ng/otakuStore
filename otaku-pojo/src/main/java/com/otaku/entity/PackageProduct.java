package com.otaku.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 套餐菜品关系
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //套餐id
    private Long packageId;

    //产品id
    private Long productId;

    //产品名称 （冗余字段）
    private String name;

    //产品原售价
    private BigDecimal price;

    //份数
    private Integer copies;
}
