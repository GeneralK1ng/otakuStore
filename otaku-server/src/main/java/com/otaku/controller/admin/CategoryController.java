package com.otaku.controller.admin;

import com.otaku.dto.CategoryDTO;
import com.otaku.dto.CategoryPageQueryDTO;
import com.otaku.entity.Category;
import com.otaku.result.PageResult;
import com.otaku.result.Result;
import com.otaku.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类相关接口")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类。
     *
     * @param categoryDTO 包含分类信息的数据传输对象
     * @return 包含操作结果的成功响应
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询。
     *
     * @param categoryPageQueryDTO 包含分类分页查询条件的数据传输对象
     * @return 包含分页查询结果的成功响应
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除分类。
     *
     * @param id 要删除的分类的唯一标识
     * @return 包含操作结果的成功响应
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result<String> deleteById(Long id){
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类。
     *
     * @param categoryDTO 包含更新后的分类信息的数据传输对象
     * @return 包含操作结果的成功响应
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用或禁用分类。
     *
     * @param status 启用（1）或禁用（0）状态
     * @param id 要操作的分类的唯一标识
     * @return 包含操作结果的成功响应
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用分类")
    public Result<String> startOrStop(@PathVariable("status") Integer status, Long id){
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据类型查询分类。
     *
     * @param type 分类类型
     * @return 包含分类列表的成功响应
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
