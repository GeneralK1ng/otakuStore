package com.otaku.vo;

import com.otaku.entity.ProductFlavor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO implements Serializable {

    private Long id;
    //产品名称
    private String name;
    //产品分类id
    private Long categoryId;
    //产品价格
    private BigDecimal price;
    //进货价格
    private BigDecimal purchasePrice;
    //图片
    private String image;
    //描述信息
    private String description;
    //0 停售 1 起售
    private Integer status;
    //更新时间
    private LocalDateTime updateTime;
    //分类名称
    private String categoryName;
    //产品关联的口味
    private List<ProductFlavor> flavors = new ArrayList<>();

    //private Integer copies;
}
