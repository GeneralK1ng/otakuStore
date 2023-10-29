package com.otaku.service;

import com.otaku.dto.OrdersSubmitDTO;
import com.otaku.vo.OrderSubmitVO;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
