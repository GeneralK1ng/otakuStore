package com.otaku.controller.admin;

import com.otaku.dto.BagItemDTO;
import com.otaku.result.Result;
import com.otaku.service.BackpackItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
