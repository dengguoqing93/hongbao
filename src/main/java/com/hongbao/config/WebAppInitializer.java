package com.hongbao.config;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support
        .AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

/**
 * @author guoqing
 * @since ： 2018/3/18 22:09
 * description:
 */
public class WebAppInitializer extends
        AbstractAnnotationConfigDispatcherServletInitializer {
    //Spring Ioc环境配置
    @Nullable
    @Override
    protected Class<?>[] getRootConfigClasses() {
        //配置spring Ioc资源
        return new Class<?>[]{RootConfig.class};
    }

    //DispatcherServlet环境配置
    @Nullable
    @Override
    protected Class<?>[] getServletConfigClasses() {
        //加载java配置类
        return new Class<?>[]{WebConfig.class};
    }

    //dispatchServlet拦截请求配置
    @Override
    protected String[] getServletMappings() {
        return new String[]{"*.do"};
    }

    //上传文件配置
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic dynamic) {
        //配置上传文件路径
        String filePath = "e:/mvc/uploads";
        //5Mb
        Long singleMax = (long) (5 * Math.pow(2, 20));
        //10MB
        Long totalMax = (long) (10 * Math.pow(2, 20));
        //设置上传文件配置
        dynamic.setMultipartConfig(new MultipartConfigElement(filePath, singleMax,
                totalMax, 0));
    }
}
