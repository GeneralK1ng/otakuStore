package com.otaku.controller.user;

import com.otaku.service.PackageService;
import com.otaku.constant.StatusConstant;
import com.otaku.entity.Package;
import com.otaku.result.Result;
import com.otaku.vo.ProductItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userPackageController")
@RequestMapping("/user/package")
@Api(tags = "C端-套餐浏览接口")
public class PackageController {
    @Autowired
    private PackageService packageService;

    /**
     * 条件查询
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "packageCache", key = "#categoryId") // key: packageCache::xxx
    public Result<List<Package>> list(Long categoryId) {
        Package aPackage = new Package();
        aPackage.setCategoryId(categoryId);
        aPackage.setStatus(StatusConstant.ENABLE);

        List<Package> list = packageService.list(aPackage);
        return Result.success(list);
    }

    /**
     * 根据套餐id查询包含的产品列表
     *
     * @param id
     * @return
     */
    @GetMapping("/product/{id}")
    @ApiOperation("根据套餐id查询包含的产品列表")
    public Result<List<ProductItemVO>> productList(@PathVariable("id") Long id) {
        List<ProductItemVO> list = packageService.getProductItemById(id);
        return Result.success(list);
    }
}
