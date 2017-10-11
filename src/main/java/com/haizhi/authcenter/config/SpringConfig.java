package com.haizhi.authcenter.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haizhi on 2017/10/9.
 */
@ComponentScan(basePackages = "com.haizhi.authcenter")
@EnableAspectJAutoProxy
@Configuration
@PropertySource({
        "classpath:/prop/jdbc.properties"
        ,"classpath:/prop/http.properties"
        ,"classpath:/prop/redis.properties"
})
@Import({
        SpringConfigMVC.class,
        SecurityConfig.class
})
public class SpringConfig implements EnvironmentAware,ApplicationContextAware{

    private Environment env;

    private ApplicationContext applicationContext;

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name="dataSource",destroyMethod="close",initMethod = "init")
    public DruidDataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(this.env.getProperty("mysql.driverClass"));
        dataSource.setUrl(this.env.getProperty("mysql.jdbcUrl"));
        dataSource.setUsername(this.env.getProperty("mysql.username"));
        dataSource.setPassword(this.env.getProperty("mysql.password"));
        dataSource.setValidationQuery(this.env.getProperty("mysql.testQuery"));

        List<Filter> filterList = new ArrayList<>();
        filterList.add(slf4jLogFilter());
        dataSource.setProxyFilters(filterList);

        dataSource.setDefaultAutoCommit(true);


        return dataSource;
    }

    @Bean
    public Slf4jLogFilter slf4jLogFilter(){
        Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
        return slf4jLogFilter;
    }

    @Bean(name="sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory(){
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();

        Resource[] resources = null;
        Resource mybatisConfig = null;
        try {
            resources = new PathMatchingResourcePatternResolver().getResources(
                    "classpath*:mapper/**/*.xml");
            mybatisConfig = new PathMatchingResourcePatternResolver().getResource(
                    "classpath:mybatis/mybatis-config.xml");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sqlSessionFactory.setMapperLocations(resources);
        sqlSessionFactory.setConfigLocation(mybatisConfig);
        sqlSessionFactory.setDataSource(dataSource());

        return sqlSessionFactory;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.haizhi.authcenter.dao.mapper");
        return mapperScannerConfigurer;
    }

    /*
    @Bean(name="txManager")
    public DataSourceTransactionManager txManager(){
        DataSourceTransactionManager txManager = new DataSourceTransactionManager();
        txManager.setDataSource(dataSource());
        return txManager;
    }*/

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean(){
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setStaticMethod(
                "org.apache.shiro.SecurityUtils.setSecurityManager");
        methodInvokingFactoryBean.setArguments(
                new Object[]{this.applicationContext.getBean(SecurityManager.class)});
        return methodInvokingFactoryBean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
