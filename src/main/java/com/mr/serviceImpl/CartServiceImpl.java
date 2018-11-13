package com.mr.serviceImpl;

import com.mr.mapper.CartMapper;
import com.mr.model.TMallShoppingCar;
import com.mr.service.CartService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YinShuai on 2018/11/9.
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    public List<TMallShoppingCar> listCartByUserId(Integer id) {
        return cartMapper.listCartByUserId(id);
    }

    public void saveCart(TMallShoppingCar cart) {
        cartMapper.saveCart(cart);
    }

    public void updateCartBySkuIdAndUserId(Map<String, Object> cartMap) {
        cartMapper.updateCartBySkuIdAndUserId(cartMap);
    }

    public TMallShoppingCar findCartBySkuIdAndUserId( Integer skuId, Integer userId) {
        return cartMapper.findCartBySkuIdAndUserId(skuId,userId);
    }

    public void updateCartShfxzBySkuIdAndUserId(Integer skuId, Integer userId, String shfxz) {
        Map<String,Object> map = new HashMap();
        map.put("skuId",skuId);
        map.put("userId",userId);
        map.put("shfxz",shfxz);
        cartMapper.updateCartShfxzBySkuIdAndUserId(map);
    }

}
