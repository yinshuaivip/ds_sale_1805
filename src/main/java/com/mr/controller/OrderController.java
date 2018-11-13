package com.mr.controller;

import com.mr.model.*;
import com.mr.service.OrderService;
import com.mr.util.MyDateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by YinShuai on 2018/11/12.
 */
@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * 核对页面
     * @return
     */
    @RequestMapping("toCheckOrder")
    public String toCheckOrder(HttpSession session, ModelMap map){
        TMallUserAccount user = (TMallUserAccount)session.getAttribute("user");

        if(user==null){//没有登录
            return "redirect:toLoginPage.do?loginSuccessUrl=toCheckOrder";
        }else{//登录
            //通过当前用户查询地址
            List<TMallAddress> addressList = orderService.listAddressByYhId(user.getId());

            //从redis中获取当前用户的购物车数据
            List<TMallShoppingCar> cartList =
                    (List<TMallShoppingCar>)redisTemplate.opsForValue().get("redisCartListUser"+user.getId());

            List<TMallShoppingCar> checkOrderList = new ArrayList<TMallShoppingCar>();

            for (int i = 0; i < cartList.size(); i++) {
                TMallShoppingCar cart = cartList.get(i);
                //如果被选中则添加在集合中
                if(cart.getShfxz().equals("1")){//被选中
                    checkOrderList.add(cart);
                }
            }
            map.put("addressList",addressList);
            map.put("checkOrderList",checkOrderList);
            map.put("sum",CartController.getSum(cartList));
            //去核对页面
            return "checkOrder";
        }
    }

    /**
     * 保存订单
     * @return
     */
    @RequestMapping("saveOrder")
    public String saveOrder(TMallAddress address ,HttpSession session){
        TMallUserAccount user = (TMallUserAccount) session.getAttribute("user");

        //从redis中获取当前用户的购物车数据
        List<TMallShoppingCar> cartList =
                (List<TMallShoppingCar>)redisTemplate.opsForValue().get("redisCartListUser"+user.getId());

        //实体类：一个订单，多个物流信息，每个物流信息中有多个订单详情
        TMallOrderVO orderVO = new TMallOrderVO();
        orderVO.setJdh(1);
        orderVO.setZje(CartController.getSum(cartList).doubleValue());
        orderVO.setYhId(user.getId());
        orderVO.setDzhId(address.getId());
        orderVO.setDzhMch(address.getDzMch());
        orderVO.setShhr(address.getShjr());


        //物流信息
        List<TMallFlowVO> flowList = new ArrayList<TMallFlowVO>();
        //存放库存地址的集合
        Set<String> flowSet = new HashSet<String>();
        //拆单：根据不用的库存地址来进行拆分
        for (int i = 0; i < cartList.size(); i++) {
            String kcdz = cartList.get(i).getKcdz();
            flowSet.add(kcdz);
        }

        Iterator<String> flowIterator = flowSet.iterator();
        while(flowIterator.hasNext()){
            String nextKcdz = flowIterator.next();//朝阳/海淀
            TMallFlowVO flowVO = new TMallFlowVO();
            flowVO.setPsfsh("顺丰物流");
            flowVO.setPsshj(MyDateUtil.getMyDateD(new Date(), 1));
            flowVO.setPsmsh("配送描述：风里雨里，东门等你！");
            flowVO.setYhId(user.getId());

            //订单详情集合
            List<TMallOrderInfo> infoList = new ArrayList<TMallOrderInfo>();
            for (int i = 0; i < cartList.size(); i++) {// a、b、c、d
                TMallOrderInfo info = new TMallOrderInfo();
                TMallShoppingCar car = cartList.get(i);

                if(car.getKcdz().equals(nextKcdz)){
                    info.setSkuJg(car.getSkuJg());
                    info.setSkuShl(car.getTjshl());
                    info.setSkuKcdz(car.getKcdz());
                    info.setGwchId(car.getId());
                    info.setSkuId(car.getSkuId());
                    info.setSkuMch(car.getSkuMch());
                    info.setShpTp(car.getShpTp());

                    infoList.add(info);
                }
            }


            //在物流信息中添加info信息
            flowVO.setInfoList(infoList);
            //将每一个符合地址的数据存放在物流集合中
            flowList.add(flowVO);
        }

        orderVO.setFlowList(flowList);

        orderService.saveOrder(orderVO);

        //更新 reids
        redisTemplate.delete("redisCartListUser"+user.getId());

        //跳转到支付页面
        return "redirect:pay.do";
    }

    @RequestMapping("pay")
    public String pay(){

        return "pay";
    }

}
