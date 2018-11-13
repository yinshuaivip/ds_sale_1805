package com.mr.controller;

import com.mr.model.TMallShoppingCar;
import com.mr.model.TMallUserAccount;
import com.mr.service.CartService;
import com.mr.util.MyCookieUtils;
import com.mr.util.MyJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YinShuai on 2018/11/7.
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 保存购物车
     * cookieName : cookieCartList
     * @CookieValue("key")：从cookie对象中获取名为key的cookie值
     *  将list对象转为json字符串 存放在cookie中
     * @param cart
     * @param session
     * @return
     */
    @RequestMapping("saveCart")
    public String saveCart(TMallShoppingCar cart, HttpSession session,ModelMap map,
                           @CookieValue(value = "cookieCartList" ,required = false)String cookieCartList,
                           HttpServletRequest request, HttpServletResponse response){
        cart.setHj(getHj(cart));

        //定义购物车集合
        List<TMallShoppingCar> cartList = new ArrayList();

        //判断是否登录
        TMallUserAccount user = (TMallUserAccount)session.getAttribute("user");
        if(user==null){//未登陆
            if(StringUtils.isBlank(cookieCartList)){//cookie为空：直接添加在cookie中
                //将数据添加到list集合
                cartList.add(cart);
            }else{//cookie不为空，则需要判断cookie中是否有相同的数据进行更新
                //先获得cookie中的数据 判断cookie中的数据 有没有跟前台传过来的cart中一样的sku
                cartList = MyJsonUtil.jsonToList(cookieCartList, TMallShoppingCar.class);
                   boolean b1 = false;
                for (int i = 0; i < cartList.size(); i++) {
                    if(cartList.get(i).getSkuId() == cart.getSkuId()){
                        b1 = true;
                    }
                }
                if(b1){//存在
                    //更新：拿到购物车中的数据，循环，更新
                    for (int i = 0; i < cartList.size(); i++) {
                        if(cartList.get(i).getSkuId() == cart.getSkuId()){
                            cartList.get(i).setTjshl(cartList.get(i).getTjshl() + cart.getTjshl());

//                            BigDecimal jg = new BigDecimal(cartList.get(i).getSkuJg() + "");
//                            BigDecimal shl = new BigDecimal(cartList.get(i).getTjshl());
                            cartList.get(i).setHj(getHj(cartList.get(i)));
                        }
                    }
                }else{//不存在
                    //将数据添加到list集合
                    cartList.add(cart);
                }
            }
            //添加到cookie中
            MyCookieUtils.setCookie(request,response,"cookieCartList",
                    MyJsonUtil.objectToJson(cartList),3*24*60*60, true);
        }else{//已登陆

            //获取数据库数据
            cartList = cartService.listCartByUserId(user.getId());
            //判断数据库中是否有数据 当前用户
            if(cartList != null || cartList.size() > 0){//有数据
                //循环遍历 查看新增的购物车是否跟数据库有相同的商品数据
                boolean b2 = false;
                for (int i = 0; i < cartList.size(); i++) {
                    if(cartList.get(i).getSkuId() == cart.getSkuId()){
                        b2 = true;
                    }
                }

                if(b2){//存在相同的数据
                    //更新数据
                    for (int i = 0; i < cartList.size(); i++) {
                        if(cartList.get(i).getSkuId() == cart.getSkuId()){
                            //修改数量
                            Map<String,Object> cartMap = new HashMap();
                            cartMap.put("skuId",cartList.get(i).getId());
                            cartMap.put("userId",user.getId());
                            cartMap.put("tjshl",cartList.get(i).getTjshl() + cart.getTjshl());
                            //修改合计
//                            BigDecimal jg = new BigDecimal(cartList.get(i).getSkuJg() + "");
//                            BigDecimal shl = new BigDecimal(cartList.get(i).getTjshl());
                            cartMap.put("hj",getHj(cartList.get(i)));

                            cartService.updateCartBySkuIdAndUserId(cartMap);
                        }
                    }

                }else{
                    //添加数据
                    cart.setYhId(user.getId());
                    cartService.saveCart(cart);
                }

            }else{//没数据
                //添加数据
                cart.setYhId(user.getId());
                cartService.saveCart(cart);
            }

            //更新 redis(清除redis中cart的list,当前用户)
            redisTemplate.delete("redisCartListUser"+user.getId());

            //用户登录之后，将数据保存在redis中
            //当前用户的key如何确定
            //redisTemplate.opsForValue().set("redisCartListUser"+user.getId(),cartList);
        }

        map.put("cart",cart);
        return "cart-success";
    }

    /***保存购物车 调用此方法
     *用BigDecimal   计算商品合计(商品价格)
     */
    public static Double getHj(TMallShoppingCar cart){

        BigDecimal jg = new BigDecimal(cart.getSkuJg() + "");
        BigDecimal thShl = new BigDecimal(cart.getTjshl());

        double hj = thShl.multiply(jg).doubleValue();
        return hj;
    }

    /**
     * 查询mini购物
     * @return
     */
    @RequestMapping("findMiniCart")
    public String findMiniCart(HttpSession session, ModelMap map,
                               @CookieValue(value = "cookieCartList",required = false)String cookieCartList){
        List<TMallShoppingCar> cartList = new ArrayList();
        //判断是否登录
        TMallUserAccount user = (TMallUserAccount)session.getAttribute("user");
        if(user==null){//未登陆
            cartList = MyJsonUtil.jsonToList(cookieCartList, TMallShoppingCar.class);

        }else{//已登录

            //从redis中获取数据
            cartList = (List<TMallShoppingCar>)redisTemplate.opsForValue().get("redisCartListUser"+user.getId());
            //redis中没有数据
            if(cartList == null || cartList.size() == 0){
                //去数据库中查询购物车表 通过用户id查询
                cartList = cartService.listCartByUserId(user.getId());
                //给redis同步数据
                redisTemplate.opsForValue().set("redisCartListUser"+user.getId(),cartList);
            }
        }

        Integer countNum = 0;
        for (int i = 0; i < cartList.size(); i++) {
           countNum +=  cartList.get(i).getTjshl();
        }

        map.put("cartList",cartList);
        map.put("countNum",countNum);
        map.put("hjSum",getSum(cartList));
        return "miniCartInner";
    }

    /**查询mini购物调用此方法
     * 用BigDecimal  计算商品数量
     */
    public static  BigDecimal getSum(List<TMallShoppingCar> cartList){
        BigDecimal sum = new BigDecimal("0");
        for (int i = 0; i < cartList.size(); i++) {
            if(cartList.get(i).getShfxz().equals("1")){//如果选择
                sum = sum.add(new BigDecimal(cartList.get(i).getHj() + ""));
            }
        }
        return sum;
    }



    /**
     * 跳转购物车页面
     * @return
     */
    @RequestMapping("toCartListPage")
    public String toCartListPage(HttpSession session, ModelMap map,
                                 @CookieValue(value = "cookieCartList",required = false)String cookieCartList){
        List<TMallShoppingCar> cartList = new ArrayList();
        //判断是否登录
        TMallUserAccount user = (TMallUserAccount)session.getAttribute("user");
        if(user==null){//未登陆
            cartList = MyJsonUtil.jsonToList(cookieCartList, TMallShoppingCar.class);

        }else{//已登录

            //从redis中获取数据
            cartList = (List<TMallShoppingCar>)redisTemplate.opsForValue().get("redisCartListUser"+user.getId());
            //redis中没有数据
            if(cartList == null || cartList.size() == 0){
                //去数据库中查询购物车表 通过用户id查询
                cartList = cartService.listCartByUserId(user.getId());
                //给redis同步数据
                redisTemplate.opsForValue().set("redisCartListUser"+user.getId(),cartList);
            }
        }

        /*Integer countNum = 0;
        for (int i = 0; i < cartList.size(); i++) {
            countNum +=  cartList.get(i).getTjshl();
        }*/

        map.put("cartList",cartList);
//        map.put("countNum",countNum);
        map.put("hjSum",getSum(cartList));
        return "cartList";
    }




    /**
     * 根据skuId修改对象的选中状态，并且刷新合计
     * @param skuId
     * @param shfxz
     * @param map
     * @return
     */
    @RequestMapping("changeShfxz")
    public String changeShfxz(HttpServletRequest request,HttpServletResponse response,
            int skuId,String shfxz,ModelMap map,HttpSession session,
                              @CookieValue(value = "cookieCartList",required = false)String cookieCartList){

        List<TMallShoppingCar> cartList = new ArrayList();
        //判断是否登录
        TMallUserAccount user = (TMallUserAccount)session.getAttribute("user");

        if(user != null){//登录
            //通过skuId 修改 cart
            //从reids中获取到数据
            cartList =  (List<TMallShoppingCar>)redisTemplate.opsForValue().get("redisCartListUser"+user.getId());

            //更新数据库
            for (int i = 0; i < cartList.size(); i++) {
                if(cartList.get(i).getSkuId() == skuId){
                    //修改数据库的状态
                    cartService.updateCartShfxzBySkuIdAndUserId(skuId,user.getId(),shfxz);
                    //修改
                    cartList.get(i).setShfxz(shfxz);
                }
            }
            //同步redis中
            redisTemplate.opsForValue().set("redisCartListUser"+user.getId(),cartList);

        }else {//未登录
            cartList = MyJsonUtil.jsonToList(cookieCartList, TMallShoppingCar.class);
            for (int i = 0; i < cartList.size(); i++) {
                if(cartList.get(i).getSkuId() == skuId){
                    cartList.get(i).setShfxz(shfxz);
                }
            }

            //更新购物车
            MyCookieUtils.setCookie(request,response,"cookieCartList",
                    MyJsonUtil.objectToJson(cartList),3*24*60*60,true);
        }

        map.put("cartList",cartList);
        map.put("hjSum",getSum(cartList));
        return "cartListInner";
    }
}
