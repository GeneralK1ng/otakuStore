package com.otaku.service;

import com.otaku.vo.OrderReportVO;
import com.otaku.vo.SalesTop10ReportVO;
import com.otaku.vo.TurnoverReportVO;
import com.otaku.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 统计指定时间区间内的营业额数据。
     *
     * @param begin 开始日期
     * @param end 结束日期
     * @return 营业额统计报告对象（VO），包含了指定时间范围内的统计数据
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间范围内的用户数据报告。
     *
     * @param begin 统计开始日期
     * @param end 统计结束日期
     * @return 一个包含用户数据统计信息的 UserReportVO 对象
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间范围内的订单数据报告。
     *
     * @param begin 开始日期
     * @param end 结束日期
     * @return 一个包含订单数据统计信息的 OrderReportVO 对象
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间范围内的销量排名top10报告。
     *
     * @param begin 开始日期
     * @param end 结束日期
     * @return 一个包含订单数据统计信息的 SalesTop10ReportVO 对象
     */
    SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end);
}
