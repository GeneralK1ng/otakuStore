package com.otaku.service;

import com.otaku.dto.ProductDTO;
import com.otaku.dto.ProductPageQueryDTO;
import com.otaku.result.PageResult;

public interface ProductService {

    /**
     * 新增产品和偏好数据
     * @param productDTO
     */
    public void saveWithFlavor(ProductDTO productDTO);


    /**
     * 产品分页查询
     * @param productPageQueryDTO
     * @return
     */
    PageResult pageQuery(ProductPageQueryDTO productPageQueryDTO);
}
