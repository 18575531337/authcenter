package com.haizhi.authcenter.controller;

import com.haizhi.authcenter.cache.Cache;
import com.haizhi.authcenter.entity.User;
import com.haizhi.authcenter.constants.RoleType;
import com.haizhi.authcenter.entity.response.RespData;

import com.haizhi.authcenter.service.UserService;
import com.haizhi.authcenter.util.Utils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haizhi on 2017/9/2.
 */
@RestController
public class UserController {

    //@Resource(name = "cacheCommon")
    private ValueOperations<String,String> valueOperations;

    @Resource(name = "cacheCommon")
    private Cache<String,String> cacheCommon;

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

        user = this.userService.findUser(user.getUsername());
        String token = Utils.generateToken(user);

        Map<String,String> resp = new HashMap<>();
        resp.put("token", token);

        this.cacheCommon.set("user_session_"+user.getId(),token,
                Utils.getExpireDate(12,Calendar.HOUR).getTimeInMillis());
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("userID",user.getId());
        /**
         * 不设置就用全局的

        session.setTimeout(Utils.getExpireDate(12, Calendar.HOUR).getTimeInMillis());
         */
        return RespData.SUCCESS().setData(resp);
    }

    @RequestMapping(value = "/logout" , method = RequestMethod.GET
            /*,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE*/)
    public RespData logout(){
        this.userService.logout();
        return RespData.SUCCESS().setData("logout");
    }

    //@RequiresPermissions("CCC")
    @RequiresRoles(RoleType.VIP)
    @RequestMapping("/getToken")
    public RespData getToken(){
        return RespData.SUCCESS().setData("你好");
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @RequestMapping("/test")
    public RespData test(){
        boolean hasRole = Utils.hasRole(RoleType.ADMIN);
        String userName = Utils.getUserName();
        return RespData.SUCCESS().setData(this.userService.test());
    }

    public void setCacheCommon(Cache<String, String> cacheCommon) {
        this.cacheCommon = cacheCommon;
    }

    public void setValueOperations(ValueOperations<String, String> valueOperations) {
        this.valueOperations = valueOperations;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
