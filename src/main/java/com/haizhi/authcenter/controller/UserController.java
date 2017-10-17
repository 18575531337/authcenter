package com.haizhi.authcenter.controller;

import com.haizhi.authcenter.entity.User;
import com.haizhi.authcenter.constants.RoleType;
import com.haizhi.authcenter.entity.response.RespData;

import com.haizhi.authcenter.service.UserService;
import com.haizhi.authcenter.util.Utils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by haizhi on 2017/9/2.
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     *  {
     *      username:"aaa",
     *      password:"a720e01a52babc386cc588766cd914c953d1804a010610acef4af82e9096cece" //bbb
     *  }
     */
    @RequestMapping(value = "/login" , method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RespData login(@RequestBody User user){
        this.userService.login(user);

        Map<String,String> resp = new HashMap<>();
        resp.put("token", Utils.generateToken(
                Long.valueOf(this.userService.findUser(user.getUsername()).getId())));
        return RespData.SUCCESS().setData(resp);
    }

    //@RequiresPermissions("CCC")
    @RequiresRoles(RoleType.VIP)
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
