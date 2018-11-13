package com.mr.controller;

import com.mr.model.TMallSku;
import com.mr.model.TMallSkuItemVO;
import com.mr.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by YinShuai on 2018/11/7.
 */
@Controller
public class ItemController {

    @Autowired
    private SkuService skuService;

    @RequestMapping("toItemPage")
    public String toItemPage(Integer skuId, Integer spuId, ModelMap map){

        //sku的数据
        TMallSkuItemVO itemvo = skuService.listItemBySkuId(skuId);

        //skuId查询到的sku集合
        List<TMallSku> skuList = skuService.listSkuBySpuId(spuId);

        map.put("itemvo",itemvo);
        map.put("skuList",skuList);
        return "item";
    }

}
