package com.haizhi.authcenter.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
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
public class SpringConfig implements EnvironmentAware {

    private Environment env;

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

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
