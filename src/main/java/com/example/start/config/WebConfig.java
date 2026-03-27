package com.example.start.config;

import com.example.start.interceptor.FeatureBlockInterceptor;
import com.example.start.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/okr/**",
                        "/users/**")
                .excludePathPatterns(
                        "/",
                        "/login",
                        "/signup",
                        "/logout",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/images/**",
                        "/favicon.ico",
                        "/error"
                );

        registry.addInterceptor(new FeatureBlockInterceptor())
                .addPathPatterns(
                        "/posts/**",
                        "/admin/**"

                );
    }
}