package com.otaku.controller.admin;


import com.otaku.result.Result;
import com.otaku.service.WorkSpaceService;
import com.otaku.vo.BusinessDataVO;
import com.otaku.vo.OrderOverViewVO;
import com.otaku.vo.PackageOverViewVO;
import com.otaku.vo.ProductOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Api(tags = "工作台相关接口")
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 用于获取工作台今日数据的控制器方法
     *
     * @return 返回包含工作台今日数据的结果对象
     */
    @GetMapping("/businessData")
    @ApiOperation(value = "工作台今日数据查询")
    public Result<BusinessDataVO> businessData(){
        // 获取今天的开始时间（00:00:00）和结束时间（23:59:59）
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        // 调用工作台服务的方法来获取业务数据
        BusinessDataVO businessDataVO = workSpaceService.getBusinessData(begin, end);
        // 返回包含业务数据的成功结果
        return Result.success(businessDataVO);
    }

    /**
     * 查询订单管理数据的控制器方法。
     *
     * @return 返回包含订单管理数据的结果对象。
     */
    @GetMapping("/overviewOrders")
    @ApiOperation(value = "查询订单管理数据")
    public Result<OrderOverViewVO> orderOverView(){
        // 调用工作台服务的方法来获取订单管理概览数据
        return Result.success(workSpaceService.getOrderOverView());
    }

    /**
     * 查询产品总览的控制器方法。
     *
     * @return 返回包含产品总览数据的结果对象。
     */
    @GetMapping("/overviewProducts")
    @ApiOperation(value = "查询产品总览")
    public Result<ProductOverViewVO> productOverView(){
        // 调用工作台服务的方法来获取产品总览数据
        return Result.success(workSpaceService.getProductOverView());
    }

    /**
     * 查询套餐总览的控制器方法。
     *
     * @return 返回包含套餐总览数据的结果对象。
     */
    @GetMapping("/overviewPackage")
    @ApiOperation(value = "查询套餐总览")
    public Result<PackageOverViewVO> packageOverView(){
        return Result.success(workSpaceService.getPackageOverView());
    }

}
