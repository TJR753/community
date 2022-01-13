package com.example.community.config;

import com.example.community.controller.interceptor.LoginInterceptor;
import com.example.community.controller.interceptor.LoginRequiredInterceptor;
import com.example.community.controller.interceptor.TestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
//    @Autowired
//    private TestInterceptor testInterceptor;

    @Autowired
    private LoginInterceptor loginInterceptor;
//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(testInterceptor).excludePathPatterns("/**/*.css","/**/*.png","/**/*.js");
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/**/*.css","/**/*.png","/**/*.js");
//        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/**/*.css","/**/*.png","/**/*.js");
    }
}
