package com.otaku.controller.user;

import com.otaku.constant.StatusConstant;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userProductController")
@RequestMapping("/user/product")
@Slf4j
@Api(tags = "C端-产品浏览接口")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 根据分类id查询产品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询产品")
    public Result<List<ProductVO>> list(Long categoryId) {

        //构造redis当中的key，规则：product_{分类id}
        String cacheKey = "product_" + categoryId;
        //查询redis当中是否存在产品数据
        List<ProductVO> cachedData = (List<ProductVO>) redisTemplate.opsForValue().get(cacheKey);
        //如果存在，直接返回，无需进行数据库查询
        if (cachedData != null) {
            // 如果缓存中有数据，直接返回
            return Result.success(cachedData);
        } else {
            // 如果缓存中没有数据，查询数据库
            Product product = new Product();
            product.setCategoryId(categoryId);
            product.setStatus(StatusConstant.ENABLE); // 查询起售中的产品

            List<ProductVO> list = productService.listWithFlavor(product);

            // 将查询结果放入Redis缓存
            redisTemplate.opsForValue().set(cacheKey, list);

            return Result.success(list);
        }
    }

    /**
     * 产品分页查询
     *
     * @param productPageQueryDTO 分页查询数据
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation(value = "产品分页查询")
    public Result<PageResult> page(ProductPageQueryDTO productPageQueryDTO) {
        log.info("产品分页查询：{}", productPageQueryDTO);
        PageResult pageResult = productService.pageQuery(productPageQueryDTO);
        return Result.success(pageResult);
    }

}
