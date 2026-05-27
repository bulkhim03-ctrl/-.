package com.itproger.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config implements WebMvcConfigurer {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Универсальный вариант для Windows
        String path = "file:///" + uploadPath.replace("\\", "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(path);
        
        System.out.println("📁 Upload path: " + path);
    }
}