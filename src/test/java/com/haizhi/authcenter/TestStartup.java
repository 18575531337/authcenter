package com.haizhi.authcenter;

import com.haizhi.authcenter.constants.Permission;
import com.haizhi.authcenter.constants.RoleType;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;



/**
 * Created by haizhi on 2017/10/9.
 */
public class TestStartup {

    @Test
    public void testLogin(){
        Subject subject = login("admin", "123");

        Assert.assertTrue(subject.isAuthenticated()); //断言用户已经登录

        //6、退出
        subject.logout();
    }

    private Subject login(String username,String password){
        //1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
        Factory<SecurityManager> factory =
                new IniSecurityManagerFactory("classpath:shiro.ini");
        //2、得到SecurityManager实例 并绑定给SecurityUtils
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        //3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);

        try {
            //4、登录，即身份验证
            subject.login(token);
        } catch (AuthenticationException e) {
            //5、身份验证失败
        }
        return subject;
    }

    @Test
    public void testRole(){
        Subject user = login("admin", "123");

        Assert.assertTrue(user.hasRole(RoleType.GUIDE));

        Assert.assertTrue(user.isPermitted(Permission.UPDATE));


    }
}
