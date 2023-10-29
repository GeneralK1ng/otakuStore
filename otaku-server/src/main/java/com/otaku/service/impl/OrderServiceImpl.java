package com.otaku.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.otaku.constant.MessageConstant;
import com.otaku.context.BaseContext;
import com.otaku.dto.*;
import com.otaku.entity.*;
import com.otaku.exception.AddressBookBusinessException;
import com.otaku.exception.OrderBusinessException;
import com.otaku.exception.ShoppingCartBusinessException;
import com.otaku.mapper.*;
import com.otaku.result.PageResult;
import com.otaku.service.OrderService;
import com.otaku.utils.WeChatPayUtil;
import com.otaku.vo.OrderPaymentVO;
import com.otaku.vo.OrderStatisticsVO;
import com.otaku.vo.OrderSubmitVO;
import com.otaku.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WeChatUserMapper weChatUserMapper;


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


        // TODO 通过地图API校验用户的收货地址是否超出配送范围

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

    /**
     * 处理订单支付，生成预支付交易单并返回支付信息。
     *
     * @param ordersPaymentDTO 包含支付所需信息的数据传输对象
     * @return 包含支付信息的 OrderPaymentVO 对象
     * @throws Exception 如果支付过程中出现异常
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 获取当前登录用户的ID
        Long userId = BaseContext.getCurrentId();
        User user = weChatUserMapper.getById(userId);

        // 根据订单号查询订单金额
        BigDecimal orderAmount = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber()).getAmount();

        // 调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                orderAmount, //支付金额，单位 元
                "OtakuStore Order", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        // 将返回的 JSON 对象转换为 OrderPaymentVO 对象
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 处理支付成功后，更新订单状态以反映付款完成。
     *
     * @param outTradeNo 支付订单号
     */
    public void paySuccess(String outTradeNo) {

        // 根据支付订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单ID更新订单的状态、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED) // 设置订单状态为待确认
                .payStatus(Orders.PAID)          // 设置支付状态为已支付
                .checkoutTime(LocalDateTime.now())// 设置结账时间为当前时间
                .build();

        orderMapper.update(orders);
    }

    /**
     * 用户端订单分页查询，根据页码、每页大小和订单状态筛选。
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param status   订单状态
     * @return 包含查询结果的分页结果对象
     */
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        // 创建订单分页查询数据传输对象，并设置用户ID和订单状态
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 执行分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入 OrderVO 进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 获取指定订单ID的订单详细信息，包括订单本身和相关的商品或套餐明细。
     *
     * @param id 要查询的订单ID
     * @return 包含订单详细信息的 OrderVO 对象
     */
    @Override
    public OrderVO details(Long id) {
        // 根据订单ID查询订单信息
        Orders orders = orderMapper.getById(id);
        // 查询该订单对应的商品或套餐明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
        // 将订单信息及其相关明细封装到 OrderVO 对象中并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;

    }

    @Override
    @Transactional
    public void userCancelById(Long id) throws Exception {
        //根据ID查询订单
        Orders ordersDB = orderMapper.getById(id);
        //校验订单是否存在
        if (ordersDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (ordersDB.getStatus() > 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        //订单处于接单状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            //调用微信支付退款接口
            String refund = weChatPayUtil.refund(
                    ordersDB.getNumber(),  //商户订单号
                    ordersDB.getNumber(),  //商户退款单号
                    new BigDecimal(String.valueOf(ordersDB.getAmount())),  //退款金额， 单位 ￥ / $
                    new BigDecimal(String.valueOf(ordersDB.getAmount())));//原订单金额
            // TODO 这里可能会出现JSON解析出错，后续需要看情况更改
            // 使用fastjson库解析JSON字符串
            DefaultJSONParser parser = new DefaultJSONParser(refund);
            JSONObject jsonObject = parser.parseObject();
            // 获取status字段的值
            String status = jsonObject.getString("status");
            if (Objects.equals(status, "SUCCESS")) {
                //支付状态修改为 退款
                orders.setPayStatus(Orders.REFUND);
            } else {
                throw new OrderBusinessException(MessageConstant.REFUND_FAILED);
            }
        }

        //更新订单状态，取消原因，取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }


    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetition(Long id) {
        //查询当前用户的id
        Long userId = BaseContext.getCurrentId();

        //根据订单id查询当前订单的详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        //将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x ->{
            ShoppingCart shoppingCart = new ShoppingCart();

            //将原订单详情里面的商品信息重新复制到购物车对象当中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        //将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 执行订单搜索操作，根据指定条件查询订单列表。
     *
     * @param ordersPageQueryDTO 用于指定搜索条件的数据传输对象
     * @return 包含查询结果的分页结果对象
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 使用PageHelper开启分页功能，指定页码和每页大小
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        // 调用订单数据访问层的pageQuery方法执行查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        //部分订单状态，需要额外返回订单商品信息，将Orders转换为OrderVO对象
        List<OrderVO> orderVOList = getOrderVoList(page);

        return new PageResult(page.getTotal(), orderVOList);
    }

    /**
     * 将订单对象列表转换为包含订单产品信息的 OrderVO 对象列表。
     *
     * @param page 包含订单数据的分页对象
     * @return 包含订单产品信息的 OrderVO 对象列表
     */
    private List<OrderVO> getOrderVoList(Page<Orders> page) {
        //需要返回订单产品信息，自定义OrderVO响应结果
        // 创建用于存储 OrderVO 对象的列表
        List<OrderVO> orderVOList = new ArrayList<>();
        // 从分页结果中获取订单对象列表
        List<Orders> ordersList = page.getResult();
        // 检查订单列表是否为空
        if (!CollectionUtils.isEmpty(ordersList)){
            for (Orders orders : ordersList) {
                // 创建一个新的 OrderVO 对象并复制共享字段
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                // 获取订单产品信息的字符串表示
                String orderProducts = getOrderProductsStr(orders);

                // 将订单产品信息封装到 OrderVO 对象中，并添加到 OrderVO 列表
                orderVO.setOrderProducts(orderProducts);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 根据订单对象获取包含商品信息的字符串。
     *
     * @param orders 包含订单信息的订单对象
     * @return 包含订单商品信息的字符串
     */
    private String getOrderProductsStr(Orders orders) {
        // 查询订单产品详情信息（订单中的商品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将每一条订单商品信息拼接为字符串（格式为：“刻晴倒模*n”）
        List<String> orderProductList = orderDetailList.stream().map(x -> {
            return x.getName() + "*" + x.getNumber() + ";";
        }).collect(Collectors.toList());

        // 将订单中的所有商品信息拼接在一起
        return String.join("", orderProductList);
    }


    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {

        //根据状态，分别查询 待接单，待派送（已接单），派送中 的订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        //将查询出来的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);

        return orderStatisticsVO;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(ordersConfirmDTO.getStatus())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     * @throws Exception
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        //根据ID查询订单
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());

        //订单只有存在，且状态为2（待接单）才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //支付状态
        Integer payStatus = ordersDB.getPayStatus();
        if (Objects.equals(payStatus, Orders.PAID)){
            //用户已经支付，需要退款
            String refund = weChatPayUtil.refund(
                    ordersDB.getNumber(),
                    ordersDB.getNumber(),
                    ordersDB.getAmount(),
                    ordersDB.getAmount());
            log.info("申请退款：{}",refund);
        }

        //拒单需要退款，根据订单ID更新订单状态，拒单原因，取消时间
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);
    }


    /**
     * 取消订单
     * @param ordersCancelDTO
     * @throws Exception
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());

        //支付状态
        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus == 1) {
            //用户已支付，需要退款
            String refund = weChatPayUtil.refund(
                    ordersDB.getNumber(),
                    ordersDB.getNumber(),
                    ordersDB.getAmount(),
                    ordersDB.getAmount());
            log.info("申请退款：{}", refund);
        }

        // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        //根据ID查询订单
        Orders ordersDB = orderMapper.getById(id);

        //校验订单是否存在，并且状态为 3已接单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        //更新订单状态，状态改变为派送中
        orderMapper.update(orders);

    }

    @Override
    public void complete(Long id) {
        //根据ID查询订单
        Orders ordersDB = orderMapper.getById(id);

        //校验订单是否存在，并且状态为 4派送中
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 更新订单状态,状态转为完成
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(orders);
    }
}
