package com.springer.semantic.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class MultipartResolverBuilder {
    @Bean @Scope(value = WebApplicationContext.SCOPE_SESSION,
           proxyMode = ScopedProxyMode.TARGET_CLASS)
    public CommonsMultipartResolver getMultipartResolver() {
        CommonsMultipartResolver mr = new CommonsMultipartResolver();
        return mr;
    }
}