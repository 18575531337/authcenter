package com.haizhi.authcenter.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * Created by haizhi on 2017/10/9.
 */
@ComponentScan(basePackages = "com.haizhi.authcenter")
@EnableAspectJAutoProxy
@Configuration
@Import({
        SpringConfigMVC.class
})
public class SpringConfig {
}
