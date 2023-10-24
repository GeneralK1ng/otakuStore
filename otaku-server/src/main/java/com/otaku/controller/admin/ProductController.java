package com.otaku.controller.admin;

import com.otaku.dto.ProductDTO;
import com.otaku.result.Result;
import com.otaku.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
