package com.otaku.mapper;


import com.otaku.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);
}
