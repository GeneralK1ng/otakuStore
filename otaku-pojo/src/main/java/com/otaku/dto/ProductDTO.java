package com.otaku.dto;

import com.otaku.entity.ProductFlavor;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductDTO implements Serializable {

    private Long id;
    //产品名称
    private String name;
    //产品分类id
    private Long categoryId;
    //产品价格
    private BigDecimal price;
    //图片
    private String image;
    //描述信息
    private String description;
    //0 停售 1 起售
    private Integer status;
    //口味
    private List<ProductFlavor> flavors = new ArrayList<>();

}
