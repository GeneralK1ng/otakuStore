package com.otaku.service.impl;

import com.otaku.dto.GoodsSalesDTO;
import com.otaku.entity.Orders;
import com.otaku.mapper.OrderMapper;
import com.otaku.mapper.UserMapper;
import com.otaku.service.ReportService;
import com.otaku.vo.OrderReportVO;
import com.otaku.vo.SalesTop10ReportVO;
import com.otaku.vo.TurnoverReportVO;
import com.otaku.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 计算给定日期范围内的每一天的营业额统计信息。
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 包含日期列表和相应营业额列表的 TurnoverReportVO 对象
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 创建一个列表用于存放从开始日期到结束日期范围内的每一天的日期
        List<LocalDate> dateList = new ArrayList<>();

        // 添加开始日期到列表
        dateList.add(begin);

        // 逐日迭代，直到结束日期
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 创建一个列表用于存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();

        // 遍历日期列表以计算每天的营业额
        for (LocalDate date : dateList) {
            // 创建每一天的开始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 创建一个映射以存储查询参数
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            // 查询每一天的营业额并添加到列表中
            Double turnover = orderMapper.sumByMap(map);
            // 处理空值，将null转换为0.0
            turnover = turnover == null ? 0.0 : turnover;
            // 添加计算的营业额到列表
            turnoverList.add(turnover);
        }

        // 创建一个 TurnoverReportVO 对象来存储日期列表和营业额列表
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                    .dateList(StringUtils.join(dateList, ","))
                    .turnoverList(StringUtils.join(turnoverList, ","))
                    .build();

        return turnoverReportVO;
    }

    /**
     * 统计时间区间内的用户数据
     * @param begin 起始日期
     * @param end 结束日期
     * @return 返回用户统计数据的视图对象
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 存放从开始到结束的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        // 生成日期列表，从开始日期递增至结束日期
        while (!begin.equals(end)){
           begin = begin.plusDays(1);
           dateList.add(begin);
        }

        // TODO: 优化建议 - 考虑使用Java 8+的日期时间API来简化日期的生成和处理

        //新增用户
        List<Integer> newUserList = new ArrayList<>();
        //总用户
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            // 创建当日的开始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 构建查询条件
            Map<String, Object> map = new HashMap<>();
            map.put("end", endTime);

            // 统计截止日期的总用户数
            Integer totalUser = userMapper.countByMap(map);

            map.put("begin", beginTime);

            // 统计当天新增用户数
            Integer newUser = userMapper.countByMap(map);

            // 添加到统计列表
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        // 构建用户报告对象
        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();

        return userReportVO;
    }

    /**
     * 统计指定时间范围内的订单数据报告。
     *
     * @param begin 统计开始日期
     * @param end 统计结束日期
     * @return 一个包含订单数据统计信息的 OrderReportVO 对象
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 存放从开始到结束的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        // 生成日期列表，从开始日期递增至结束日期
        // TODO: 优化建议 - 使用 Java 8 的日期时间操作来生成日期列表，以提高代码的可读性和简洁性
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            // 查询每天的订单总数
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Integer orderCount = getOrderCount(beginTime, endTime, null);
            // 查询每天的有效订单数
            Integer validorderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validorderCount);
        }
        // 计算总订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        // 计算有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        // 计算订单完成率
        Double orderCompletionRate = (totalOrderCount != 0) ? (validOrderCount / (double) totalOrderCount) : 0.0;

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))// 将日期列表转换为逗号分隔的字符串
                .orderCountList(StringUtils.join(orderCountList, ","))// 将订单总数列表转换为逗号分隔的字符串
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))// 将有效订单数列表转换为逗号分隔的字符串
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();

        return orderReportVO;
    }

    /**
     * 根据条件统计订单数量。
     *
     * @param begin 订单开始时间
     * @param end 订单结束时间
     * @param status 订单状态 (null 表示不限制状态)
     * @return 符合条件的订单数量
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status){
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }

    /**
     * 统计指定时间范围内的销量排行 top 10 报告。
     *
     * @param begin 统计开始日期
     * @param end 统计结束日期
     * @return 一个包含销量排行 top 10 的 SalesTop10ReportVO 对象
     */
    @Override
    public SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end) {
        // 创建开始和结束的日期时间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 查询销量排行 top 10 数据
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);

        // 查询销量排行 top 10 数据
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        // 构建销量排行 top 10 报告对象
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
