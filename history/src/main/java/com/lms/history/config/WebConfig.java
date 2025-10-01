package com.lms.history.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // '/images/store/' 경로 매핑
        registry.addResourceHandler("/images/store/**")
                .addResourceLocations("file:///C:/parkdotori/webDev/workspace/history/src/main/resources/static/images/store/");

        // '/uploads/' 경로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/board_images/");

        Path uploadRoot = Paths.get("uploads").toAbsolutePath().normalize();
        String location = "file:" + uploadRoot.toString() + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}