package com.haizhi.authcenter.controller;

import com.haizhi.authcenter.response.RespData;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by haizhi on 2017/9/2.
 */
@RestController
public class UserController {

    @RequiresRoles("admin")
    @RequestMapping("/getToken")
    public RespData getToken(){
        return RespData.SUCCESS().setData("你好");
    }
}