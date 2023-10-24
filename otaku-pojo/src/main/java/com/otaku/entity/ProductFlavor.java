package com.otaku.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 偏好口味
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFlavor implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    //产品id
    private Long dishId;

    //偏好名称
    private String name;

    //偏好数据list
    private String value;

}
