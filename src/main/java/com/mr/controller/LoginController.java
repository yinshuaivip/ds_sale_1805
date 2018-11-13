package com.mr.controller;

import com.mr.model.TMallShoppingCar;
import com.mr.model.TMallUserAccount;
import com.mr.service.CartService;
import com.mr.service.LoginService;
import com.mr.util.MyCookieUtils;
import com.mr.util.MyJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YinShuai on 2018/11/5.
 */
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private CartService cartService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录
     */
    @RequestMapping("login")
    public String login(String yhMch, String yhMm, HttpSession session,String loginSuccessUrl,
                        HttpServletRequest request, HttpServletResponse response,
                        @CookieValue(value = "cookieCartList" ,required = false)String cookieCartList){
       //通过用户名密码去查询对象
       TMallUserAccount user = loginService.login(yhMch,yhMm);
       //如果不存在返回登录  如果存在跳转到主页
       if(user==null){
            return "redirect:toLoginPage.do";
       }else {
           session.setAttribute("user",user);
           MyCookieUtils.setCookie(request,response,"yhMch",user.getYhMch(),24*60*60,true);

           //更新购物车
           //从cookie中查询数据

           if(!StringUtils.isBlank(cookieCartList)){//有数据
               //将cookie中的数据添加在 db
               List<TMallShoppingCar> cartListCookie = MyJsonUtil.jsonToList(cookieCartList, TMallShoppingCar.class);

               //循环cookie
               for (int i = 0; i < cartListCookie.size(); i++) {//cookie的
                   //根据当前对象的skuid和用户id查询数据  判断当前对象是否重复
                   TMallShoppingCar cart =
                           cartService.findCartBySkuIdAndUserId(cartListCookie.get(i).getSkuId(),user.getId());

                   if(cart != null){//重复
                       //更新
                       Map<String, Object> cartMap = new HashMap<String, Object>();
                       cartMap.put("skuId",cartListCookie.get(i).getSkuId());
                       cartMap.put("userId",user.getId());

                       //修改对象的数量
                       cartMap.put("tjshl",cartListCookie.get(i).getTjshl()+cart.getTjshl());
                       cartListCookie.get(i).setTjshl(cartListCookie.get(i).getTjshl()+cart.getTjshl());

                       cartMap.put("hj",CartController.getHj(cartListCookie.get(i)));

                       cartService.updateCartBySkuIdAndUserId(cartMap);

                   }else{
                       //添加当前对象
                       cartListCookie.get(i).setYhId(user.getId());
                       cartService.saveCart(cartListCookie.get(i));
                   }
               }

               //清空cookie中的购物车
               MyCookieUtils.deleteCookie(request, response, "cookieCartList");

               //清空redis中的购物车
               redisTemplate.delete("redisCartListUser"+user.getId());
           }

           if(!StringUtils.isBlank(loginSuccessUrl)){
                return "redirect:"+loginSuccessUrl+".do";
           }

           return "redirect:toMainPage.do";
       }
    }

    //注销 删除session中的user
    @RequestMapping("logOut")
    public String logOut(HttpSession session){
        session.removeAttribute("user");
        return "redirect:toLoginPage.do";
    }
}
