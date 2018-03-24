package com.hongbao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guoqing
 * @since ： 2018/3/19 19:44
 * description:
 */

@Configuration
@ComponentScan(value = "com.*", includeFilters = {@ComponentScan.Filter(type =
        FilterType.ANNOTATION, value = Controller.class)})
@EnableAsync
public class WebConfig {
    /**
     * 通过注解@bean初始化视图解析器
     *
     * @return ViewResolver 视图解析器
     */


    @Bean(name = "requestMappingHandlerAdapter")
    public HandlerAdapter initRequestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();
        MappingJackson2HttpMessageConverter jsonConverter = new
                MappingJackson2HttpMessageConverter();
        MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(mediaType);
        jsonConverter.setSupportedMediaTypes(mediaTypes);
        handlerAdapter.getMessageConverters().add(jsonConverter);
        return handlerAdapter;
    }

}
