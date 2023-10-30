package com.otaku.service;

import com.otaku.vo.BusinessDataVO;
import com.otaku.vo.OrderOverViewVO;
import com.otaku.vo.PackageOverViewVO;
import com.otaku.vo.ProductOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {

    /**
     * 用于获取工作台今日数据
     *
     * @return 返回包含工作台今日数据的结果对象
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询订单管理数据
     *
     * @return 返回包含订单管理数据的结果对象。
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询产品总览
     *
     * @return 返回包含产品总览数据的结果对象
     */
    ProductOverViewVO getProductOverView();

    /**
     * 查询套餐总览
     *
     * @return 返回包含套餐总览数据的结果对象
     */
    PackageOverViewVO getPackageOverView();

}
