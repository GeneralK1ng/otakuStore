package com.otaku.controller.admin;

import com.otaku.dto.BagItemDTO;
import com.otaku.dto.BagItemPageQueryDTO;
import com.otaku.result.PageResult;
import com.otaku.result.Result;
import com.otaku.service.BackpackItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/backpack")
@Api(tags = "背包相关接口")
@Slf4j
public class BackpackItemController {

    @Autowired
    private BackpackItemService backpackItemService;

    /**
     * 新增背包物品
     * @param bagItemDTO 新增背包物品信息
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation(value = "新增背包物品")
    public Result save(@RequestBody BagItemDTO bagItemDTO){
        log.info("新增背包物品 {}", bagItemDTO);
        backpackItemService.save(bagItemDTO);

        return Result.success();
    }

    /**
     * 管理端批量删除背包物品
     * @param ids 物品id列表
     *              注意：物品id列表不能为null
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "管理端批量删除背包物品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除背包物品 {}", ids);
        backpackItemService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询背包物品")
    public Result<PageResult> pageQuery(BagItemPageQueryDTO bagItemPageQueryDTO) {
        log.info("分页查询背包物品 {}", bagItemPageQueryDTO);
        PageResult pageResult = backpackItemService.pageQuery(bagItemPageQueryDTO);
        return Result.success(pageResult);
    }
}
