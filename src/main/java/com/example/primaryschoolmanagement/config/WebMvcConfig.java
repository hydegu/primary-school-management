package com.example.primaryschoolmanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Web MVC 配置
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private FileUploadProperties fileUploadProperties;

    /**
     * 配置静态资源访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取上传文件的绝对路径
        String uploadPath = Paths.get(fileUploadProperties.getUploadDir())
                .toAbsolutePath()
                .normalize()
                .toString();

        // 配置静态资源映射
        // URL访问路径：/uploads/**
        // 实际文件路径：file:/{uploadPath}/
        registry.addResourceHandler(fileUploadProperties.getAccessUrlPrefix() + "/**")
                .addResourceLocations("file:" + uploadPath + "/");

        log.info("静态资源映射配置成功：{} -> file:{}/",
                fileUploadProperties.getAccessUrlPrefix(), uploadPath);
    }
}
