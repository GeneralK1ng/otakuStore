package com.otaku.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.otaku.constant.MessageConstant;
import com.otaku.constant.StatusConstant;
import com.otaku.dto.PackageDTO;
import com.otaku.dto.PackagePageQueryDTO;
import com.otaku.entity.Package;
import com.otaku.entity.PackageProduct;
import com.otaku.exception.DeletionNotAllowedException;
import com.otaku.mapper.PackageMapper;
import com.otaku.mapper.PackageProductMapper;
import com.otaku.mapper.ProductMapper;
import com.otaku.result.PageResult;
import com.otaku.service.PackageService;
import com.otaku.vo.PackageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务的接口实现类
 */
@Service
@Slf4j
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageMapper packageMapper;
    @Autowired
    private PackageProductMapper packageProductMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 新增套餐，同时保持产品和套餐之间的绑定关系
     * @param packageDTO
     */
    @Override
    @Transactional
    public void saveWithProduct(PackageDTO packageDTO) {
        Package aPackage = new Package();
        BeanUtils.copyProperties(packageDTO,aPackage);

        //向套餐表当中写入数据
        packageMapper.insert(aPackage);
        //获取生成套餐的ID
        Long aPackageId = aPackage.getId();

        List<PackageProduct> packageProducts = packageDTO.getPackageProducts();

        packageProducts.forEach(packageProduct -> {
            packageProduct.setPackageId(aPackageId);
        });

        //保存套餐和产品之间的绑定关系
        packageProductMapper.insertBatch(packageProducts);

    }

    /**
     * 分页查询
     * @param packagePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(PackagePageQueryDTO packagePageQueryDTO) {
        int pageNum = packagePageQueryDTO.getPage();
        int pageSize = packagePageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum,pageSize);
        Page<PackageVO> page = packageMapper.pageQuery(packagePageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            Package aPackage = packageMapper.getById(id);
            if(StatusConstant.ENABLE == aPackage.getStatus()){
                //正在售卖的套餐无法删除
                throw new DeletionNotAllowedException(MessageConstant.PACKAGE_ON_SALE);
            }
        });

        ids.forEach(packageId -> {
            //删除套餐表中的数据
            packageMapper.deleteById(packageId);
            //删除套餐产品关系表中的数据
            packageProductMapper.deleteBySetmealId(packageId);
        });
    }
}
