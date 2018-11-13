package com.mr.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by YinShuai on 2018/11/5.
 */
@Controller
public class IndexControll {

    //跳转到主页
    @RequestMapping("toMainPage")
    public String toMainPage(){
        return "index";
    }

    //跳转到登录页面
    @RequestMapping("toLoginPage")
    public String toLoginPage(String loginSuccessUrl, ModelMap map){
        if(!StringUtils.isBlank(loginSuccessUrl)){
            map.put("loginSuccessUrl",loginSuccessUrl);
        }

        return "login";
    }



}
