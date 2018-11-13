package com.mr.mapper;

import com.mr.model.TMallShoppingCar;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by YinShuai on 2018/11/9.
 */
public interface CartMapper {
    List<TMallShoppingCar> listCartByUserId(@Param("userId") Integer userId);

    void saveCart(TMallShoppingCar cart);

    void updateCartBySkuIdAndUserId(Map<String, Object> cartMap);

    TMallShoppingCar findCartBySkuIdAndUserId(@Param("skuId")Integer skuId, @Param("userId")Integer userId);

    void updateCartShfxzBySkuIdAndUserId(Map<String, Object> map);
}
