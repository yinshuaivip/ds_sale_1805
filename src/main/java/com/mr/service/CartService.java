package com.mr.service;

import com.mr.model.TMallShoppingCar;

import java.util.List;
import java.util.Map;

/**
 * Created by YinShuai on 2018/11/9.
 */
public interface CartService {
    List<TMallShoppingCar> listCartByUserId(Integer id);

    void saveCart(TMallShoppingCar cart);

    void updateCartBySkuIdAndUserId(Map<String, Object> cartMap);

    TMallShoppingCar findCartBySkuIdAndUserId(Integer skuId, Integer id);

    void updateCartShfxzBySkuIdAndUserId(Integer skuId, Integer id, String shfxz);
}
