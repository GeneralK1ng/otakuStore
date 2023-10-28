package com.otaku.service.impl;

import com.otaku.context.BaseContext;
import com.otaku.dto.ShoppingCartDTO;
import com.otaku.entity.Package;
import com.otaku.entity.Product;
import com.otaku.entity.ShoppingCart;
import com.otaku.mapper.PackageMapper;
import com.otaku.mapper.ProductMapper;
import com.otaku.mapper.ShoppingCartMapper;
import com.otaku.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PackageMapper packageMapper;
    /**
     * 将商品添加到购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前加入购物车的商品是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //如果存在，只需将数量++
        if (!list.isEmpty()){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1); //update shopping_cart set number = ? where id = ?
            shoppingCartMapper.updateNumberById(cart);
        } else{
            //如果不存在，需要insert语句添加到购物车数据库

            //判断本次添加到购物车的是产品还是套餐
            Long productId = shoppingCartDTO.getProductId();
            if (productId != null){
                //这次添加到购物车的是产品
                Product product = productMapper.getById(productId);
                shoppingCart.setName(product.getName());
                shoppingCart.setImage(product.getImage());
                shoppingCart.setAmount(product.getPrice());

            } else {
                //这次添加到购物车的是套餐
                Long packageId = shoppingCartDTO.getPackageId();

                Package aPackage = packageMapper.getById(packageId);
                shoppingCart.setName(aPackage.getName());
                shoppingCart.setImage(aPackage.getImage());
                shoppingCart.setAmount(aPackage.getPrice());

            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartMapper.insert(shoppingCart);
        }

    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        //获取当前用户的ID
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();

        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        //获取当前用户的ID
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }


    /**
     * 删除购物车当中的一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        //设置查询的条件，查询当前用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if (!list.isEmpty()){
            shoppingCart = list.get(0);

            Integer number = shoppingCart.getNumber();

            if (number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.deleteById(shoppingCart.getId());
            } else {
                //当前商品在购物车中份数不为1，修改份数--
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }
    }
}
