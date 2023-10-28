package com.otaku.controller.admin;

import com.otaku.dto.PackageDTO;
import com.otaku.dto.PackagePageQueryDTO;
import com.otaku.result.PageResult;
import com.otaku.result.Result;
import com.otaku.service.PackageService;
import com.otaku.vo.PackageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/package")
@Api(tags = "套餐相关接口")
@Slf4j
public class PackageController {

    @Autowired
    private PackageService packageService;

    @PostMapping
    @ApiOperation(value = "新增套餐")
    @CacheEvict(cacheNames = "packageCache", key = "#packageDTO.categoryId")
    public Result save(@RequestBody PackageDTO packageDTO){
        log.info("新增套餐：{}",packageDTO);
        packageService.saveWithProduct(packageDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param packagePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(PackagePageQueryDTO packagePageQueryDTO) {
        log.info("套餐分页查询：{}", packagePageQueryDTO);
        PageResult pageResult = packageService.pageQuery(packagePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "packageCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        packageService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐，用于修改页面回显数据
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<PackageVO> getById(@PathVariable Long id) {
        PackageVO packageVO = packageService.getByIdWithProduct(id);
        return Result.success(packageVO);
    }

    /**
     * 修改套餐
     *
     * @param packageDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "packageCache", allEntries = true)
    public Result update(@RequestBody PackageDTO packageDTO) {
        packageService.update(packageDTO);
        return Result.success();
    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    @CacheEvict(cacheNames = "packageCache", allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id) {
        packageService.startOrStop(status, id);
        return Result.success();
    }
}
