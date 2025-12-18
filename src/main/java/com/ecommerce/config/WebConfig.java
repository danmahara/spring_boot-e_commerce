package com.ecommerce.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ecommerce.helpers.ImageProperties;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

        @Autowired
        AdminInterceptor adminInterceptor;

        @Autowired
        private ImageProperties imageProperties;

        @Autowired
        CustomerInterceptor customerInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                log.info("Registering AdminInterceptor...");

                registry.addInterceptor(adminInterceptor)
                                .addPathPatterns("/admin/**", "/api/admin/**")
                                .excludePathPatterns(
                                                "/admin/login",
                                                "/admin/logout",
                                                "/api/admin/login",
                                                "/api/admin/register");

                registry.addInterceptor(customerInterceptor)
                                .addPathPatterns("/dashboard/**", "/orders/**", "/profile/**")
                                .excludePathPatterns("/login", "/register", "/forgot-password", "/");

                log.info("AdminInterceptor registered successfully");
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/uploads/**")
                                .addResourceLocations("file:///" +
                                                Paths.get(imageProperties.getUploadDir())
                                                                .toAbsolutePath().toString()
                                                + "/");
        }
}
