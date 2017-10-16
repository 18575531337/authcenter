package com.haizhi.authcenter.config;

import com.haizhi.authcenter.security.UserCredentialMatcher;
import com.haizhi.authcenter.security.UserRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Created by haizhi on 2017/10/9.
 */
@Configuration
public class SecurityConfig implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    /**/
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        /*
        Factory<SecurityManager> factory =
                new IniSecurityManagerFactory("classpath:shiro.ini");
        shiroFilterFactoryBean.setSecurityManager(factory.getInstance());
        */
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        shiroFilterFactoryBean.setLoginUrl("/login");
        return shiroFilterFactoryBean;
    }

    @Bean("securityManager")
    @DependsOn({"userRealm","userCredentialMatcher"})
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        UserRealm userRealm = this.applicationContext.getBean(UserRealm.class);
        userRealm.setCredentialsMatcher(this.applicationContext.getBean(UserCredentialMatcher.class));

        securityManager.setRealm(userRealm);
        return securityManager;
    }

    //session生命周期管理器
    @Bean
    public LifecycleBeanPostProcessor lifecycle(){
        LifecycleBeanPostProcessor lifecycle = new LifecycleBeanPostProcessor();
        return lifecycle;
    }

    //ID 生成器
    @Bean
    public JavaUuidSessionIdGenerator idGenerator(){
        JavaUuidSessionIdGenerator idGenerator = new JavaUuidSessionIdGenerator();
        return idGenerator;
    }

    @Bean
    public EnterpriseCacheSessionDAO sessionDAO(){
        EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
        return sessionDAO;
    }

    @Bean
    public DefaultSessionManager sessionManager(){
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        //会话超时时间，单位：毫秒  300
        sessionManager.setGlobalSessionTimeout(1800000);

        //启用定时器 and 定时清理失效会话, 清理用户直接关闭浏览器造成的孤立会话
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionValidationInterval(120000);

        return sessionManager;
    }

    @Bean
    public SimpleCookie simpleCookie(){
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(180000);
        return simpleCookie;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
