package com.otaku.service.impl;

import com.otaku.constant.MessageConstant;
import com.otaku.context.BaseContext;
import com.otaku.dto.OrdersSubmitDTO;
import com.otaku.entity.AddressBook;
import com.otaku.entity.OrderDetail;
import com.otaku.entity.Orders;
import com.otaku.entity.ShoppingCart;
import com.otaku.exception.AddressBookBusinessException;
import com.otaku.exception.ShoppingCartBusinessException;
import com.otaku.mapper.AddressBookMapper;
import com.otaku.mapper.OrderDetailMapper;
import com.otaku.mapper.OrderMapper;
import com.otaku.mapper.ShoppingCartMapper;
import com.otaku.service.OrderService;
import com.otaku.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;


    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //处理各种业务异常（地址簿为空，购物车数据为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());

        if (addressBook == null) {
            // 处理地址簿为空的异常情况，可以抛出自定义异常或者返回相应错误信息
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }


        //查询当前用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        if (shoppingCartList.isEmpty()){
            // 处理购物车数据为空的异常情况，可以抛出自定义异常或者返回相应错误信息
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向订单表（order）插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);

        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);

        orderMapper.insert(orders);


        List<OrderDetail> orderDetailList  = new ArrayList<>();
        //向订单明细表（order_detail）插入n条数据
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail(); //订单明细
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId()); //设置当前订单明细关联的订单id
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.inserBatch(orderDetailList);
        //清空用户的购物车数据
        shoppingCartMapper.deleteByUserId(userId);
        //封装VO返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }
}
