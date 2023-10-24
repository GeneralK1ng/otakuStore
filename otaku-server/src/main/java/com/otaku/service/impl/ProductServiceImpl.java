package com.otaku.service.impl;

import com.otaku.dto.ProductDTO;
import com.otaku.entity.Product;
import com.otaku.entity.ProductFlavor;
import com.otaku.mapper.ProductFlavorMapper;
import com.otaku.mapper.ProductMapper;
import com.otaku.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductFlavorMapper productFlavorMapper;
    /**
     * 新增产品和对应的偏好
     * @param productDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(ProductDTO productDTO) {

        Product product = new Product();
        //对象数据拷贝
        BeanUtils.copyProperties(productDTO,product);

        //向产品表插入1条数据
        productMapper.insert(product);

        //获取insert语句生成的主键值
        Long productId = product.getId();

        List<ProductFlavor> flavors = productDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()){
            flavors.forEach(productFlavor -> {
                productFlavor.setProductId(productId) ;
            });
            //向偏好表插入n条数据
            productFlavorMapper.insertBatch(flavors);
        }

    }
}
