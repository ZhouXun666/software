package com.school.secondhand.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目中 `src/main/app-uploads` 的绝对路径（动态适配开发环境）
        String uploadPath = System.getProperty("user.dir") + "/src/main/app-uploads/";
        // 或直接写死绝对路径（Windows 示例）：
        // String uploadPath = "D:/文档/学习/专业课实验/backend/secondhand/src/main/app-uploads/";

        // 关键：URL前缀 `/uploads/**` 映射到 `app-uploads` 目录
        registry.addResourceHandler("/src/main/app-uploads/**") // 前端访问的 URL 前缀
                .addResourceLocations("file:" + uploadPath); // 本地目录（必须以 file: 开头）
    }
}
