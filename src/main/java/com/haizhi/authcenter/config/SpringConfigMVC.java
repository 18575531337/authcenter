package com.haizhi.authcenter.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by haizhi on 2017/10/9.
 */
@Configuration
@EnableWebMvc
public class SpringConfigMVC extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(
                Charset.forName("UTF-8"));
        stringConverter.setWriteAcceptCharset(false);
        FastJsonHttpMessageConverter fastJson = new FastJsonHttpMessageConverter();
        converters.add(fastJson);
        converters.add(stringConverter);
    }

    /*
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor());
        //registry.addInterceptor(new TestInterceptor());
    }
    */
}
