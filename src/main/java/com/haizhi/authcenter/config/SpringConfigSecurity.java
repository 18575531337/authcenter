package com.haizhi.authcenter.config;

import com.haizhi.authcenter.security.*;
import com.haizhi.authcenter.util.Utils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authz.SslFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by haizhi on 2017/10/9.
 */
@Configuration
public class SpringConfigSecurity implements ApplicationContextAware{

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
        shiroFilterFactoryBean.setSecurityManager(getSecurityManager());
        shiroFilterFactoryBean.setLoginUrl("/login");

        /*
        SslFilter sslFilter = new SslFilter();
        sslFilter.setPort(8443);
        shiroFilterFactoryBean.getFilters().put("ssl",sslFilter);
        */

        return shiroFilterFactoryBean;
    }

    @Bean("securityManager")
    @DependsOn({"userRealm","userCredentialMatcher"})
    public SecurityManager getSecurityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        UserRealm userRealm = this.applicationContext.getBean(UserRealm.class);
        userRealm.setCredentialsMatcher(this.applicationContext.getBean(UserCredentialMatcher.class));

        securityManager.setRealm(userRealm);

        //securityManager.setSessionManager(getSessionManager());

        return securityManager;
    }

    /**
     * 鉴权拦截器
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(getSecurityManager());
        return advisor;
    }

    //session生命周期管理器
    @Bean
    public LifecycleBeanPostProcessor lifecycle(){
        LifecycleBeanPostProcessor lifecycle = new LifecycleBeanPostProcessor();
        return lifecycle;
    }

    //ID 生成器
    @Bean
    public JavaUuidSessionIdGenerator getIdGenerator(){
        JavaUuidSessionIdGenerator idGenerator = new JavaUuidSessionIdGenerator();
        return idGenerator;
    }

    /*
    @Bean
    public EnterpriseCacheSessionDAO sessionDAO(){
        EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
        return sessionDAO;
    }*/

    @Bean
    @DependsOn({
            "userSessionListener",
            "userSessionDao"
    })
    public DefaultSessionManager getSessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //会话超时时间，单位：毫秒  300
        sessionManager.setGlobalSessionTimeout(
                Utils.getExpireDate(12, Calendar.HOUR).getTimeInMillis());

        //启用定时器 and 定时清理失效会话, 清理用户直接关闭浏览器造成的孤立会话
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionValidationInterval(120000);

        List<SessionListener> listenerList = new ArrayList<>();
        listenerList.add(this.applicationContext.getBean(UserSessionListener.class));

        UserSessionDao userSessionDao = this.applicationContext.getBean(UserSessionDao.class);
        userSessionDao.setSessionIdGenerator(getIdGenerator());

        sessionManager.setSessionListeners(listenerList);
        sessionManager.setSessionDAO(userSessionDao);
        sessionManager.setSessionFactory(new UserSessionFactory());

        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(getSimpleCookie());

        return sessionManager;
    }

    @Bean
    public SimpleCookie getSimpleCookie(){
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
