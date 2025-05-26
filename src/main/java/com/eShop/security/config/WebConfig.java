package com.eShop.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads").toAbsolutePath();
        String uploadPath = "file:" + uploadDir + "/";

        System.out.println("Serving static files from: " + uploadPath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}