package com.haizhi.authcenter.controller;

import com.haizhi.authcenter.bean.User;
import com.haizhi.authcenter.response.RespData;

import com.haizhi.authcenter.service.UserService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodType;

/**
 * Created by haizhi on 2017/9/2.
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login" , method = RequestMethod.POST)
    public RespData login(@RequestBody User user){
        this.userService.login(user);
        return RespData.SUCCESS().setData("登陆成功");
    }

    @RequiresRoles("admin")
    @RequestMapping("/getToken")
    public RespData getToken(){
        return RespData.SUCCESS().setData("你好");
    }

    @RequestMapping("/test")
    public RespData test(){
        return RespData.SUCCESS().setData(this.userService.test());
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
