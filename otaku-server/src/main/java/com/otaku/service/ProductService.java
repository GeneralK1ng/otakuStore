package com.otaku.service;

import com.otaku.dto.ProductDTO;
import com.otaku.dto.ProductPageQueryDTO;
import com.otaku.entity.Product;
import com.otaku.result.PageResult;
import com.otaku.vo.ProductVO;

import java.util.List;

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

    /**
     * 产品批量删除功能
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据ID查询产品和对应的偏好数据
     * @param id
     * @return
     */
    ProductVO getByIdWithFlavor(Long id);

    /**
     * 根据ID修改产品基本信息和偏好数据
     * @param productDTO
     */
    void updateWithFlavor(ProductDTO productDTO);

    /**
     * 根据分类ID查询产品
     * @param categoryId
     * @return
     */
    List<Product> list(Long categoryId);
}
