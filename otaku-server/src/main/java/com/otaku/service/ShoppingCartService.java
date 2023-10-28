package com.otaku.service;

import com.otaku.dto.ShoppingCartDTO;
import com.otaku.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 将商品添加到购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     */
    void cleanShoppingCart();

    /**
     * 删除购物车当中的一个商品
     * @param shoppingCartDTO
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
