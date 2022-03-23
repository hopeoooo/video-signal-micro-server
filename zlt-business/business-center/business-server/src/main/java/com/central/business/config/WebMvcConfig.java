package com.central.business.config;

import com.central.common.config.DefaultWebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;


@Configuration
public class WebMvcConfig extends DefaultWebMvcConfig {

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
