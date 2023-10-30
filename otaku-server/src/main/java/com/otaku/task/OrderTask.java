package com.otaku.task;


import com.otaku.entity.Orders;
import com.otaku.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 定时处理超时订单的方法
     */
    @Transactional
    @Scheduled(cron = "0 * * * * ? ") // 使用Cron表达式，每分钟触发一次
    public void processTimeoutOrder(){
        log.info("开始定时处理超时订单：{}", LocalDateTime.now());

        // 查询待支付状态的订单，且下单时间早于当前时间减去15分钟
        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));
        if (!orderList.isEmpty()){
            // 循环处理每个超时订单
            for (Orders orders : orderList) {
                // 将订单状态更新为已取消，同时记录取消原因和取消时间
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 定时处理处于派送中状态的订单的方法
     */
    @Transactional
    @Scheduled(cron = "0 0 1 * * ?") // 使用Cron表达式，每天凌晨1点触发一次
    public void processDeliveryOrder(){
        log.info("定时处理处于派送中的订单：{}", LocalDateTime.now());

        // 计算一个小时前的时间点
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        // 查询处于派送中状态且下单时间早于一小时前的订单
        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if (!orderList.isEmpty()){
            // 循环处理每个派送中的订单，将其状态更新为已完成
            for (Orders orders : orderList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }

}
