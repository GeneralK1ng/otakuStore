package com.otaku.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.otaku.constant.MessageConstant;
import com.otaku.constant.StatusConstant;
import com.otaku.dto.PackageDTO;
import com.otaku.dto.PackagePageQueryDTO;
import com.otaku.entity.Package;
import com.otaku.entity.PackageProduct;
import com.otaku.entity.Product;
import com.otaku.exception.DeletionNotAllowedException;
import com.otaku.exception.PackageEnableFailedException;
import com.otaku.mapper.PackageMapper;
import com.otaku.mapper.PackageProductMapper;
import com.otaku.mapper.ProductMapper;
import com.otaku.result.PageResult;
import com.otaku.service.PackageService;
import com.otaku.vo.PackageVO;
import com.otaku.vo.ProductItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
     * 新增套餐，同时维护套餐和产品之间的绑定关系。
     *
     * @param packageDTO 包含套餐和产品信息的数据传输对象。
     */
    @Override
    @Transactional
    public void saveWithProduct(PackageDTO packageDTO) {
        Package aPackage = new Package();
        BeanUtils.copyProperties(packageDTO,aPackage);

        // 向套餐表中插入数据
        packageMapper.insert(aPackage);
        // 获取新生成的套餐的 ID
        Long aPackageId = aPackage.getId();

        List<PackageProduct> packageProducts = packageDTO.getPackageProducts();
        // 设置每个套餐产品的套餐 ID，以建立绑定关系
        packageProducts.forEach(packageProduct -> {
            packageProduct.setPackageId(aPackageId);
        });

        // 保存套餐和产品之间的绑定关系
        packageProductMapper.insertBatch(packageProducts);

    }

    /**
     * 分页查询套餐。
     *
     * @param packagePageQueryDTO 包含分页查询条件的数据传输对象。
     * @return 返回包含分页结果的 PageResult 对象，包括总记录数和套餐信息列表。
     */
    @Override
    public PageResult pageQuery(PackagePageQueryDTO packagePageQueryDTO) {
        int pageNum = packagePageQueryDTO.getPage();
        int pageSize = packagePageQueryDTO.getPageSize();
        // 使用分页插件设置分页参数
        PageHelper.startPage(pageNum,pageSize);
        Page<PackageVO> page = packageMapper.pageQuery(packagePageQueryDTO);
        // 创建并返回包含分页结果的 PageResult 对象
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除套餐。
     *
     * @param ids 包含要删除的套餐的唯一标识 ID 的列表。
     * @throws DeletionNotAllowedException 如果要删除的套餐处于正在销售状态，将抛出此异常。
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            Package aPackage = packageMapper.getById(id);
            if(StatusConstant.ENABLE == aPackage.getStatus()){
                // 正在销售的套餐无法删除
                throw new DeletionNotAllowedException(MessageConstant.PACKAGE_ON_SALE);
            }
        });

        ids.forEach(packageId -> {
            // 从套餐表中删除数据
            packageMapper.deleteById(packageId);
            // 从套餐产品关系表中删除数据
            packageProductMapper.deleteByPackageId(packageId);
        });
    }

    /**
     * 根据套餐 ID 查询套餐和相关套餐产品的绑定关系。
     *
     * @param id 套餐的唯一标识 ID。
     * @return 返回包含套餐和绑定套餐产品的信息的 PackageVO 对象。
     */
    @Override
    public PackageVO getByIdWithProduct(Long id) {
        Package apackage = packageMapper.getById(id);
        List<PackageProduct> packageProducts = packageProductMapper.getByPackageId(id);

        PackageVO packageVO = new PackageVO();
        BeanUtils.copyProperties(apackage,packageVO);
        packageVO.setPackageProducts(packageProducts);

        return packageVO;
    }

    /**
     * 更新套餐信息，同时维护套餐和产品之间的绑定关系。
     *
     * @param packageDTO 包含套餐和产品信息的数据传输对象。
     */
    @Override
    @Transactional
    public void update(PackageDTO packageDTO) {
        Package aPackage = new Package();
        BeanUtils.copyProperties(packageDTO,aPackage);

        // 执行更新语句，修改套餐表
        packageMapper.update(aPackage);

        // 获取套餐的唯一标识 ID
        Long packageId = packageDTO.getId();

        // 删除套餐和产品之间的关系，操作package_product表，执行delete
        packageProductMapper.deleteByPackageId(packageId);

        List<PackageProduct> packageProducts = packageDTO.getPackageProducts();

        // 设置每个套餐产品的套餐 ID，以重新建立绑定关系
        packageProducts.forEach(packageProduct -> {
            packageProduct.setPackageId(packageId);
        });

        //重新插入套餐和产品的关联关系，操作package_product表，执行insert
        packageProductMapper.insertBatch(packageProducts);

    }

    /**
     * 启售或停售套餐。
     *
     * @param status 新的套餐状态，可以是 {@link StatusConstant#ENABLE}（启售）或 {@link StatusConstant#DISABLE}（停售）。
     * @param id 套餐的唯一标识 ID。
     * @throws PackageEnableFailedException 如果尝试启售套餐，但套餐内包含停售产品，将抛出此异常。
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 如果要启售套餐，检查套餐内是否包含停售产品，如果有则抛出异常
        if (Objects.equals(status, StatusConstant.ENABLE)){

            List<Product> productList = productMapper.getByPackageId(id);
            if (!productList.isEmpty()){
                productList.forEach(product -> {
                    if (StatusConstant.DISABLE.equals(product.getStatus())){
                        throw new PackageEnableFailedException(MessageConstant.PACKAGE_ENABLE_FAILED);
                    }
                });
            }
        }

        Package aPackage = Package.builder()
                .id(id)
                .status(status)
                .build();
        // 更新套餐的状态
        packageMapper.update(aPackage);
    }

    /**
     * 根据动态条件查询套餐列表。
     *
     * @param aPackage 包含动态查询条件的套餐对象。
     * @return 返回符合给定条件的套餐列表。
     */
    @Override
    public List<Package> list(Package aPackage) {
        return packageMapper.list(aPackage);
    }

    /**
     * 根据套餐 ID 查询包含的产品选项。
     *
     * @param id 套餐的唯一标识 ID。
     * @return 返回包含产品选项信息的列表，每个选项包括产品名称、份数、图片和描述。
     */
    @Override
    public List<ProductItemVO> getProductItemById(Long id) {
        return packageMapper.getProductItemByPackageId(id);
    }
}
