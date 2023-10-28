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

}
