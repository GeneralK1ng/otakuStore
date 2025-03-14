package com.otaku.controller.admin;


import com.otaku.result.Result;
import com.otaku.service.ReportService;
import com.otaku.vo.OrderReportVO;
import com.otaku.vo.SalesTop10ReportVO;
import com.otaku.vo.TurnoverReportVO;
import com.otaku.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 获取营业额统计数据。
     *
     * @param begin 开始日期（格式：yyyy-MM-dd）
     * @param end 结束日期（格式：yyyy-MM-dd）
     * @return 包含营业额统计数据的成功响应结果
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation(value = "营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额数据统计：{}，{}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }

    /**
     * 用户数量统计接口，用于获取用户数据统计报告
     * @param begin 开始日期（格式：yyyy-MM-dd）
     * @param end 结束日期（格式：yyyy-MM-dd）
     * @return 用户数据统计报告的结果
     */
    @GetMapping("/userStatistics")
    @ApiOperation(value = "用户数量统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        // 记录日志，用于跟踪统计请求
        log.info("用户数据统计：{}，{}", begin, end);
        // 调用 reportService 获取用户数据统计报告
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    /**
     * 订单统计接口，用于获取指定时间范围内的订单数据统计报告。
     *
     * @param begin 开始日期
     * @param end 结束日期
     * @return 包含订单数据统计信息的 OrderReportVO 对象
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation(value = "订单统计")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        // 记录日志，用于跟踪统计请求
        log.info("订单数据统计：{}，{}", begin, end);
        return Result.success(reportService.getOrderStatistics(begin, end));
    }

    /**
     * 获取指定日期范围内的销量排名前10的报告数据。
     *
     * @param begin 查询的开始日期，采用 "yyyy-MM-dd" 格式。
     * @param end 查询的结束日期，采用 "yyyy-MM-dd" 格式。
     * @return 返回包含销量排名前10的报告数据的 Result 对象。
     */
    @GetMapping("/top10")
    @ApiOperation(value = "销量榜前10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("销量排名top10统计：{}，{}", begin, end);
        return Result.success(reportService.getSalesTop10Statistics(begin, end));
    }

    /**
     * 导出运营数据报表。
     *
     * @param response HttpServletResponse对象，用于将导出文件写入响应流。
     */
    @GetMapping("/export")
    @ApiOperation(value = "导出数据报表")
    public void export(HttpServletResponse response){
        reportService.exportBusinessData(response);
    }
}
