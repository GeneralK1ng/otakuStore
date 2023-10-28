package com.otaku.mapper;


import com.otaku.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查询
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据ID修改商品数量
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入购物车数据
     * @param shoppingCart
     */
    @Insert("insert into  shopping_cart(name, image, user_id, product_id, package_id, product_flavor, number, amount, create_time) " +
            "values (#{name}, #{image}, #{userId}, #{productId}, #{packageId}, #{productFlavor}, #{number}, #{amount}, #{creatTime})")
    void insert(ShoppingCart shoppingCart);

}
