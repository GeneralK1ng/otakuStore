package com.otaku.service.impl;

import com.otaku.constant.StatusConstant;
import com.otaku.entity.Orders;
import com.otaku.mapper.OrderMapper;
import com.otaku.mapper.PackageMapper;
import com.otaku.mapper.ProductMapper;
import com.otaku.mapper.UserMapper;
import com.otaku.service.WorkSpaceService;
import com.otaku.vo.BusinessDataVO;
import com.otaku.vo.OrderOverViewVO;
import com.otaku.vo.PackageOverViewVO;
import com.otaku.vo.ProductOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private PackageMapper packageMapper;


    /**
     * 获取工作台今日数据，根据指定的时间范围（开始时间和结束时间）计算以下统计信息：
     * - 总订单数
     * - 订单完成率
     * - 平均订单金额
     * - 有效订单数
     * - 新用户数
     *
     * @param begin 查询数据的开始时间。
     * @param end 查询数据的结束时间。
     * @return 包含工作台今日数据的 BusinessDataVO 对象。
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        // 创建包含查询参数的 Map。
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);

        // 查询总订单数
        Integer totalOrderCount = orderMapper.countByMap(map);

        // 更新查询参数以过滤已完成的订单，并计算营业额
        map.put("status", Orders.COMPLETED);
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0.0 : turnover;

        // 查询有效订单数
        Integer validOrderCount = orderMapper.countByMap(map);

        // 初始化单位价格和订单完成率
        Double unitPrice = 0.0;
        Double orderCompletionRate = 0.0;

        // 计算单位价格和订单完成率，避免除以零的情况
        if (totalOrderCount != 0 && validOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            unitPrice = turnover / validOrderCount;
        }

        // 查询新用户数
        Integer newUsers = userMapper.countByMap(map);

        // 创建并返回包含统计数据的 BusinessDataVO 对象
        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 获取订单管理概览数据，统计不同状态下的订单数量，包括：
     * - 待确认订单数量
     * - 已确认订单数量 (待派送)
     * - 已完成订单数量
     * - 已取消订单数量
     * - 所有订单数量
     *
     * @return 包含订单管理概览数据的 OrderOverViewVO 对象。
     */
    @Override
    public OrderOverViewVO getOrderOverView() {
        // 创建包含查询参数的 Map，起始时间为今天的开始时间，状态为待确认。
        Map<String, Object> map = new HashMap<>();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
        map.put("status", Orders.TO_BE_CONFIRMED);

        // 查询待确认订单数量
        Integer waitingOrders = orderMapper.countByMap(map);

        // 更新状态为已确认，查询已确认订单数量（派送中）
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(map);

        // 更新状态为已完成，查询已完成订单数量
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);

        // 更新状态为已取消，查询已取消订单数量
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(map);

        // 清除状态过滤，查询所有订单数量
        map.put("status", null);
        Integer allOrders = orderMapper.countByMap(map);

        // 创建并返回包含订单管理概览数据的 OrderOverViewVO 对象
        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 获取产品总览数据，统计不同状态下的产品数量，包括：
     * - 已售出的产品数量
     * - 已停售的产品数量
     *
     * @return 包含产品总览数据的 ProductOverViewVO 对象。
     */
    @Override
    public ProductOverViewVO getProductOverView() {
        // 创建包含查询参数的 Map，状态为已启用
        Map<String, Object> map = new HashMap<>();
        map.put("status", StatusConstant.ENABLE);

        // 查询已售出的产品数量
        Integer sold = productMapper.countByMap(map);

        // 更新状态为已停用，查询已停售的产品数量
        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = productMapper.countByMap(map);

        // 创建并返回包含产品总览数据的 ProductOverViewVO 对象
        return ProductOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }


    /**
     * 获取套餐总览数据，统计不同状态下的套餐数量，包括：
     * - 已售出的套餐数量
     * - 已停售的套餐数量
     *
     * @return 包含套餐总览数据的 PackageOverViewVO 对象。
     */
    @Override
    public PackageOverViewVO getPackageOverView() {
        // 创建包含查询参数的 Map，状态为已启用
        Map<String, Object> map = new HashMap<>();
        // 查询已售出的套餐数量
        map.put("status", StatusConstant.ENABLE);
        Integer sold = packageMapper.countByMap(map);
        // 更新状态为已停用，查询已停售的套餐数量
        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = packageMapper.countByMap(map);
        // 创建并返回包含套餐总览数据的 PackageOverViewVO 对象
        return PackageOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
