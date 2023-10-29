package com.otaku.vo;

import com.otaku.entity.OrderDetail;
import com.otaku.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Orders implements Serializable {

    //订单产品信息
    private String orderProducts;

    //订单详情
    private List<OrderDetail> orderDetailList;

}
