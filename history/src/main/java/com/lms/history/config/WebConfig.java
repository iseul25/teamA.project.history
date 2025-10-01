package com.lms.history.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // '/images/store/' 경로를 로컬 파일 시스템의 특정 디렉터리에 매핑
        registry.addResourceHandler("/images/store/**")
                .addResourceLocations("file:///C:/parkdotori/webDev/workspace/history/src/main/resources/static/images/store/");

        // '/uploads/' 경로를 다른 로컬 디렉터리에 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/board_images/");
    }
}