package com.otaku.service;

import com.otaku.dto.ProductDTO;

public interface ProductService {

    /**
     * 新增产品和偏好数据
     * @param productDTO
     */
    public void saveWithFlavor(ProductDTO productDTO);
}
