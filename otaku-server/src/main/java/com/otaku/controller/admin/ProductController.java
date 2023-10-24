package com.otaku.controller.admin;

import com.otaku.dto.ProductDTO;
import com.otaku.dto.ProductPageQueryDTO;
import com.otaku.result.PageResult;
import com.otaku.result.Result;
import com.otaku.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品管理
 */
@RestController
@RequestMapping("/admin/product")
@Api(tags = "产品相关接口")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 新增产品
     * @param productDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增产品")
    public Result save(@RequestBody ProductDTO productDTO){
         log.info("新增产品：{}",productDTO);
         productService.saveWithFlavor(productDTO);

        return Result.success();
    }


    @GetMapping("/page")
    @ApiOperation(value = "产品分页查询")
    public Result<PageResult> page(ProductPageQueryDTO productPageQueryDTO) {
        log.info("产品分页查询：{}", productPageQueryDTO);
        PageResult pageResult = productService.pageQuery(productPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 产品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "产品批量删除操作")
    public Result delete(@RequestParam List<Long> ids){

        log.info("产品批量删除：{}",ids);

        productService.deleteBatch(ids);

        return Result.success();
    }

}
