package com.otaku.controller.admin;

import com.otaku.dto.ProductDTO;
import com.otaku.dto.ProductPageQueryDTO;
import com.otaku.entity.Product;
import com.otaku.result.PageResult;
import com.otaku.result.Result;
import com.otaku.service.ProductService;
import com.otaku.vo.ProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增产品
     * 该方法用于接收一个ProductDTO对象作为参数，其中包含了要新增的产品的相关信息。
     * 方法使用HTTP的POST方法进行请求，并将返回一个Result对象作为结果。
     * @param productDTO - 产品信息
     * @return 结果对象
     */
    @PostMapping
    @ApiOperation(value = "新增产品")
    public Result save(@RequestBody ProductDTO productDTO){
        log.info("新增产品：{}",productDTO);
        productService.saveWithFlavor(productDTO);

        // 清理redis缓存
        String key = "product_" + productDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }



    /**
     * 产品分页查询
     * @param productPageQueryDTO 查询条件
     * @return 结果对象，包含分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation(value = "产品分页查询")
    public Result<PageResult> page(ProductPageQueryDTO productPageQueryDTO) {
        log.info("产品分页查询：{}", productPageQueryDTO);
        PageResult pageResult = productService.pageQuery(productPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 产品批量删除
     * @param ids 要删除的产品ID列表
     * @return 删除结果
     */
    @DeleteMapping
    @ApiOperation(value = "产品批量删除操作")
    public Result delete(@RequestParam List<Long> ids){

        log.info("产品批量删除：{}",ids);

        productService.deleteBatch(ids);
        //将所有的产品缓存都清理
        cleanCache("product_*");
        return Result.success();
    }

    /**
     * 根据ID查询产品信息与偏好数据
     * @param id 查询的产品ID
     * @return 产品信息与偏好数据
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询产品")
    public Result<ProductVO> getById(@PathVariable Long id){
        log.info("根据ID查询产品：{}",id);
        ProductVO productVO = productService.getByIdWithFlavor(id);
        return Result.success(productVO);
    }

    /**
     * 修改产品信息
     * @param productDTO 产品信息
     * @return 修改结果
     */
    @PutMapping
    @ApiOperation(value = "修改产品信息")
    public Result update(@RequestBody ProductDTO productDTO){
        log.info("修改产品信息：{}",productDTO);
        productService.updateWithFlavor(productDTO);

        //将所有的产品缓存都清理
        cleanCache("product_*");
        return Result.success();
    }

    /**
     * 产品起售停售
     * @param status 状态，1表示起售，0表示停售
     * @param id 产品ID
     * @return 结果对象，包含操作是否成功的信息
     */
    @PostMapping("/status/{status}")
    @ApiOperation("产品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        productService.startOrStop(status,id);

        //将所有的产品缓存都清理
        cleanCache("product_*");
        return Result.success();
    }

    /**
     * 根据分类id查询产品
     * @param categoryId 分类id
     * @return 产品列表
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询产品")
    public Result<List<Product>> list(Long categoryId){
        List<Product> list = productService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 清理缓存
     * @param pattern 匹配模式
     */
    private void cleanCache(String pattern){
        // 清理缓存，根据给定的模式删除redis中的数据
        Set keys = redisTemplate.keys(pattern);
        // 根据给定的模式获取匹配的键名集合
        redisTemplate.delete(keys);
        // 删除redis中对应的所有键值对
    }


}
