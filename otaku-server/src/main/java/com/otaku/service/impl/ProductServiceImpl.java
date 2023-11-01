package com.otaku.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.otaku.constant.MessageConstant;
import com.otaku.constant.StatusConstant;
import com.otaku.dto.ProductDTO;
import com.otaku.dto.ProductPageQueryDTO;
import com.otaku.entity.Package;
import com.otaku.entity.Product;
import com.otaku.entity.ProductFlavor;
import com.otaku.exception.DeletionNotAllowedException;
import com.otaku.mapper.PackageMapper;
import com.otaku.mapper.PackageProductMapper;
import com.otaku.mapper.ProductFlavorMapper;
import com.otaku.mapper.ProductMapper;
import com.otaku.result.PageResult;
import com.otaku.service.ProductService;
import com.otaku.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductFlavorMapper productFlavorMapper;
    @Autowired
    private PackageProductMapper packageProductMapper;
    @Autowired
    private PackageMapper packageMapper;
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

    /**
     * 产品分页查询
     * @param productPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(ProductPageQueryDTO productPageQueryDTO) {
        //开始分页
        PageHelper.startPage(productPageQueryDTO.getPage(),productPageQueryDTO.getPageSize());
        //获取page对象
        Page<ProductVO> page = productMapper.pageQuery(productPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 产品批量删除
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前产品是否能够删除——是否存在正在售卖的产品，即 status=1 的产品
        for (Long id : ids) {
            Product product = productMapper.getById(id);
            if (product.getStatus().equals(StatusConstant.ENABLE)){
                //当前产品处于售卖中
                throw new DeletionNotAllowedException(MessageConstant.PRODUCT_ON_SALE);
            }
        }

        //判断当前产品是否能够删除——当前产品是否被套餐绑定，如过被绑定也无法删除
        List<Long> packageIds = packageProductMapper.getPackageIdsByProductIds(ids);
        
        if (!packageIds.isEmpty()){
            //当前产品被套餐绑定，无法删除
            throw new DeletionNotAllowedException(MessageConstant.PRODUCT_BE_RELATED_BY_PACKAGE);
        }

        //删除产品表当中的产品数据（未优化）
        /*for (Long id : ids) {
            productMapper.deleteById(id);
            //删除产品关联的偏好数据
            productFlavorMapper.deleteByProductId(id);
        }*/

        // 批量删除产品表中的产品数据
        if (!ids.isEmpty()) {
            productMapper.deleteBatch(ids);
            // 批量删除产品关联的偏好数据
            productFlavorMapper.deleteByProductIds(ids);
        }
    }

    /**
     * 根据ID查询产品信息和对应的偏好数据
     * @param id
     * @return
     */
    @Override
    public ProductVO getByIdWithFlavor(Long id) {
        //根据ID查询产品数据
        Product product = productMapper.getById(id);
        //根据ID查询偏好数据
        List<ProductFlavor> productFlavors = productFlavorMapper.getByProductId(id);
        //将查询到的数据封装到productVO
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product,productVO);
        productVO.setFlavors(productFlavors);

        return productVO;
    }

    /**
     * 根据ID修改产品基本信息和偏好数据
     * @param productDTO
     */
    @Override
    public void updateWithFlavor(ProductDTO productDTO) {

        Product product = new Product();
        BeanUtils.copyProperties(productDTO,product);

        //修改产品的基本信息
        productMapper.update(product);
        //删除原有的偏好数据
        productFlavorMapper.deleteByProductId(productDTO.getId());
        //重新插入偏好数据
        List<ProductFlavor> flavors = productDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()){
            flavors.forEach(productFlavor -> {
                productFlavor.setProductId(productDTO.getId()); ;
            });
            //向偏好表插入n条数据
            productFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据分类ID查询产品
     * @param categoryId
     * @return
     */
    @Override
    public List<Product> list(Long categoryId) {
        Product product = Product.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return productMapper.list(product);
    }

    /**
     * 启用或停用产品。
     *
     * @param status 产品状态，1表示启用，0表示停用
     * @param id 产品ID
     */
    @Override
    @Transactional
    public void startOrStop(Integer status, Long id) {
        // 创建产品对象，用于更新产品状态
        Product product = Product.builder()
                .id(id)
                .status(status)
                .build();
        // 更新产品状态
        productMapper.update(product);
        // 记录产品状态更新日志
        log.info("产品状态更新成功：ID={}, 新状态={}", id, status);

        if (Objects.equals(status, StatusConstant.DISABLE)){
            // 如果是停售操作，还需要将包含当前产品的套餐也停售
            List<Long> productIds = new ArrayList<>();
            productIds.add(id);
            // 获取包含当前产品的套餐列表
            List<Long> packageIds = packageProductMapper.getPackageIdsByProductIds(productIds);
            if (!packageIds.isEmpty()){
                for (Long packageId : packageIds) {
                    // 创建套餐对象，用于更新套餐状态
                    Package aPackage = Package.builder()
                            .id(packageId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    // 更新套餐状态
                    packageMapper.update(aPackage);
                    // 记录套餐状态更新日志
                    log.info("套餐状态更新成功：ID={}, 新状态={}", packageId, StatusConstant.DISABLE);
                }
            }
        }
    }

    /**
     * 条件查询产品和偏好
     *
     * @param product
     * @return
     */
    public List<ProductVO> listWithFlavor(Product product) {
        List<Product> productList = productMapper.list(product);

        List<ProductVO> productVOList = new ArrayList<>();

        for (Product p : productList) {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(p,productVO);

            //根据产品id查询对应的口味
            List<ProductFlavor> flavors = productFlavorMapper.getByProductId(p.getId());

            productVO.setFlavors(flavors);
            productVOList.add(productVO);
        }

        return productVOList;
    }
}
