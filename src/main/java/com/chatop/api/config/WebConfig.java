package com.chatop.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure Spring to serve files under /uploads/images/
        registry.addResourceHandler("/upload/pictures/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/upload/pictures/");
    }
}