package com.otaku.mapper;


import com.github.pagehelper.Page;
import com.otaku.dto.GoodsSalesDTO;
import com.otaku.dto.OrdersPageQueryDTO;
import com.otaku.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 向数据库插入订单数据
     * @param orders 要插入的订单对象
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     * @return 与订单号匹配的订单对象，如果不存在则返回null
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders 包含要更新的订单信息的订单对象
     */
    void update(Orders orders);

    /**
     * 分页查询并按下单时间顺序排序订单。
     *
     * @param ordersPageQueryDTO 包含查询条件的数据传输对象
     * @return 分页结果，包含按下单时间排序的订单列表
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单ID查询订单。
     *
     * @param id 订单ID
     * @return 与订单ID匹配的订单对象，如果不存在则返回null
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 查询特定状态的订单数量。
     *
     * @param status 订单状态
     * @return 订单数量
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据订单状态和下单时间查询订单。
     *
     * @param status 订单状态
     * @param orderTime 下单时间
     * @return 与指定状态和下单时间条件匹配的订单列表
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);


    /**
     * 根据动态条件统计营业额数据。
     *
     * @param map 包含动态条件的映射，用于过滤数据
     * @return 统计结果，表示营业额的总金额，可能为null
     */
    Double sumByMap(Map<String, Object> map);

    /**
     * 根据动态条件统计订单数据。
     *
     * @param map 包含动态条件的映射，用于过滤数据
     * @return 统计结果，表示订单的数据，可能为null
     */
    Integer countByMap(Map<String, Object> map);

    /**
     * 统计指定时间范围内的销量排名前10的商品数据。
     *
     * @param begin 开始时间
     * @param end 结束时间
     * @return 返回包含销量排名前10的商品数据的泛型 List，每个元素为 GoodsSalesDTO 对象
     */

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
