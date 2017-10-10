package com.haizhi.authcenter.config;

import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.Factory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haizhi on 2017/10/9.
 */
@Configuration
public class SecurityConfig {

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        Factory<SecurityManager> factory =
                new IniSecurityManagerFactory("classpath:shiro.ini");
        shiroFilterFactoryBean.setSecurityManager(factory.getInstance());
        return shiroFilterFactoryBean;
    }
}
