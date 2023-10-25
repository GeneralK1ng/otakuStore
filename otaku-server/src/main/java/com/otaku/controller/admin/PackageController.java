package com.otaku.controller.admin;

import com.otaku.dto.PackageDTO;
import com.otaku.dto.PackagePageQueryDTO;
import com.otaku.result.PageResult;
import com.otaku.result.Result;
import com.otaku.service.PackageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping(" /admin/package")
@Api(tags = "套餐相关接口")
@Slf4j
public class PackageController {

    @Autowired
    private PackageService packageService;

    @PostMapping
    @ApiOperation(value = "新增套餐")
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
    public Result delete(@RequestParam List<Long> ids){
        packageService.deleteBatch(ids);
        return Result.success();
    }
}
